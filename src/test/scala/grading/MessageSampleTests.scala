package grading

import edu.unh.cs.mc.utils.Resources
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.Iterable

class MessageSampleTests extends AnyFunSuite with Resources {

  import cs735_835.noc.Message

  import scala.jdk.CollectionConverters._

  test("message properly initialized") {
    val m1 = new Message(1, 1, 2, 3, 4, 5)
    val m2 = new Message(2, 1, 2, 3, 4, 5)
    assert(m1 !== m2)
    assert(m1.getId !== m2.getId)
    assert(m1.getId === m1.##)
    assert(m2.getId === m2.##)
    assert(m1.sourceRow() === 2)
    assert(m1.sourceCol() === 3)
    assert(m1.destRow() === 4)
    assert(m1.destCol() === 5)
  }

  test("tracking set and get") {
    val m1 = new Message(1, 5, 4, 3, 2, 1)
    val m2 = new Message(2, 1, 2, 3, 4, 5)
    assert(!m1.isTracked)
    assert(!m2.isTracked)
    m1.setTracked(true)
    assert(m1.isTracked)
    assert(!m2.isTracked)
    m1.setTracked(false)
    assert(!m1.isTracked)
    assert(!m2.isTracked)
  }

  test("send and receive, single thread [3pts]") {
    val m1 = new Message(1, 5, 4, 3, 2, 1)
    val m2 = new Message(2, 1, 2, 3, 4, 5)
    assert(m1.getScheduledTime === 5)
    assert(m2.getScheduledTime === 1)
    assert(m1.getSendTime === -1)
    assert(m2.getSendTime === -1)
    assert(m1.getReceiveTime === -1)
    assert(m2.getReceiveTime === -1)
    assert(!m1.hasBeenSent)
    assert(!m1.hasBeenReceived)
    assert(!m2.hasBeenSent)
    assert(!m2.hasBeenReceived)
    m1.send(7)
    assert(m1.getSendTime === 7)
    assert(m2.getSendTime === -1)
    assert(m1.getReceiveTime === -1)
    assert(m2.getReceiveTime === -1)
    assert(m1.hasBeenSent)
    assert(!m1.hasBeenReceived)
    assert(!m2.hasBeenSent)
    assert(!m2.hasBeenReceived)
    m1.receive(10)
    assert(m1.getSendTime === 7)
    assert(m2.getSendTime === -1)
    assert(m1.getReceiveTime === 10)
    assert(m2.getReceiveTime === -1)
    assert(m1.hasBeenSent)
    assert(m1.hasBeenReceived)
    assert(!m2.hasBeenSent)
    assert(!m2.hasBeenReceived)
  }

  test("toString") {
    val m = new Message(42, 5, 4, 3, 2, 1)
    val id = m.getId
    assert(m.toString === s"msg $id (never sent)")
    m.send(11)
    assert(m.toString === s"msg $id sent by (4, 3) at 11 (never delivered)")
    m.receive(20)
    assert(m.toString === s"msg $id sent by (4, 3) at 11, delivered to (2, 1) at 20")
  }

  test("exception (constructor)") {
    assertThrows[IllegalArgumentException](new Message(1, -5, 4, 3, 2, 1))
  }

  test("exception (receive without send)") {
    val m = new Message(1, 5, 4, 3, 2, 1)
    assertThrows[IllegalStateException](m.receive(10))
  }

  def checkMessages(messages: Iterable[Message]) = {
    assert(messages.size === 2)
    val Seq(m1, m2) = messages.toSeq
    assert(m1.getId === 735)
    assert(m2.getId === 835)
    assert(m1.getScheduledTime === 100)
    assert(m2.getScheduledTime === 101)
    assert(m1.getSendTime === -1)
    assert(m2.getSendTime === -1)
    assert(m1.getReceiveTime === -1)
    assert(m2.getReceiveTime === -1)
    assert(m1.sourceRow === 1)
    assert(m2.sourceRow === 4)
    assert(m1.sourceCol === 2)
    assert(m2.sourceCol === 3)
    assert(m1.destRow === 3)
    assert(m2.destRow === 2)
    assert(m1.destCol === 4)
    assert(m2.destCol === 1)
    assert(!m1.isTracked)
    assert(m2.isTracked)
  }

  test("parsing (correct)") {
    val url = getResource("messages1.txt")
    checkMessages(Message.readMessagesFromURL(url).asScala)
  }
}
