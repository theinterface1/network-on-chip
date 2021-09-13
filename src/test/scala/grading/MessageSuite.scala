package grading

import org.scalatest.tagobjects.Slow
import org.scalatest.time.SpanSugar._

class MessageSuite extends MessageSampleTests with GradingSuite {

  import java.util.concurrent.CountDownLatch

  import cs735_835.noc.Message
  import edu.unh.cs.mc.utils.threads._

  import scala.concurrent.Future
  import scala.jdk.CollectionConverters._

  override val longTimeLimit = 30.seconds

  test("exception (illegal send)") {
    val m = new Message(1, 5, 4, 3, 2, 1)
    assertThrows[IllegalArgumentException](m.send(-10))
  }

  test("exception (illegal receive)") {
    val m = new Message(1, 5, 4, 3, 2, 1)
    m.send(10)
    assertThrows[IllegalArgumentException](m.receive(-20))
  }

  test("exception (double send)") {
    val m = new Message(1, 5, 4, 3, 2, 1)
    m.send(10)
    assertThrows[IllegalStateException](m.send(11))
  }

  test("exception (double receive)") {
    val m = new Message(1, 5, 4, 3, 2, 1)
    m.send(10)
    m.receive(20)
    assertThrows[IllegalStateException](m.receive(21))
  }

  test("exception (receive before send)") {
    val m = new Message(1, 5, 4, 3, 2, 1)
    m.send(10)
    assertThrows[IllegalArgumentException](m.receive(9))
  }

  for ((n, m) <- List((10, 1000), (10, 100000), (100, 1000), (100, 100000), (250, 1000), (250, 10000))) {

    test(s"thread-safety of send (n=$n, m=$m)", Slow) {
      withLocalContext(newUnlimitedThreadPool) { implicit exec =>
        val messages = Array.tabulate(m)(i => new Message(i + 1, i + 1, 2, 3, 4, 5))
        val start = new CountDownLatch(n)

        val failures = Future.traverse(List.range(0, n)) { _ =>
          Future {
            var f = 0
            start.countDown()
            start.await()
            for (i <- messages.indices) try {
              messages(i).send(i + 1)
            }
            catch {
              case _: IllegalStateException => f += 1
            }
            f
          }
        }
        for (l <- failures) yield {
          assert(l.sum === m * (n - 1))
          for (m <- messages) {
            assert(m.hasBeenSent)
            assert(m.getSendTime === m.getScheduledTime)
          }

        }
      }
    }

    test(s"thread-safety of receive (n=$n, m=$m)", Slow) {
      withLocalContext(newUnlimitedThreadPool) { implicit exec =>
        val messages = Array.tabulate(m) { i =>
          val m = new Message(i + 1, i + 1, 2, 3, 4, 5)
          m.send(i + 1)
          m
        }
        val start = new CountDownLatch(n)

        val failures = Future.traverse(List.range(0, n)) { _ =>
          Future {
            var f = 0
            start.countDown()
            start.await()
            for (i <- messages.indices) try {
              messages(i).receive(i + 11)
            }
            catch {
              case _: IllegalStateException => f += 1
            }
            f
          }
        }
        for (l <- failures) yield {
          assert(l.sum === m * (n - 1))
          for (m <- messages) {
            assert(m.hasBeenReceived)
            assert(m.getReceiveTime === m.getScheduledTime + 10)
          }

        }
      }
    }
  }

  test("parsing (whitespace)") {
    val url = getResource("messages2.txt")
    checkMessages(Message.readMessagesFromURL(url).asScala)
  }
}
