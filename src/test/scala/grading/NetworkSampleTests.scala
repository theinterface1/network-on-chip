package grading

import org.scalactic.TimesOnInt._
import org.scalatest.funsuite.AnyFunSuite

class NetworkSampleTests extends AnyFunSuite {

  import cs735_835.noc._

  import scala.jdk.CollectionConverters._

  class Clock(var time: Int = 0) extends cs735_835.noc.Clock {
    def getTime: Int = time
  }

  def simpleCoreProcessingTests(time: Int) = {
    test(s"simple core processing, 1 message (time=$time)") {
      val clock = new Clock
      val net = new Network(10, 10)
      net.setClock(clock)
      val core = net.getCore(5, 5)
      val m = new Message(1, time, 5, 5, 5, 6)
      assert(!net.isActive)
      assert(!core.isActive)
      net.injectMessage(m)
      assert(core.isActive)
      assert(net.isActive)
      assert(!m.hasBeenSent)
      assert(!m.hasBeenReceived)
      time times {
        core.process()
        assert(!m.hasBeenSent)
        assert(!m.hasBeenReceived)
        clock.time += 1
      }
      core.process()
      assert(m.hasBeenSent)
      assert(!m.hasBeenReceived)
      assert(core.isActive)
      assert(net.isActive)
    }

    test(s"simple core processing, 2 messages (time=$time)") {
      val clock = new Clock
      val net = new Network(10, 10)
      net.setClock(clock)
      val core = net.getCore(5, 5)
      val m1 = new Message(1, time, 5, 5, 5, 6)
      val m2 = new Message(2, time, 5, 5, 6, 5)
      assert(!net.isActive)
      assert(!core.isActive)
      net.injectMessage(m1)
      net.injectMessage(m2)
      assert(core.isActive)
      assert(net.isActive)
      assert(!m1.hasBeenSent)
      assert(!m1.hasBeenReceived)
      assert(!m2.hasBeenSent)
      assert(!m2.hasBeenReceived)
      time times {
        core.process()
        assert(!m1.hasBeenSent)
        assert(!m1.hasBeenReceived)
        assert(!m2.hasBeenSent)
        assert(!m2.hasBeenReceived)
        clock.time += 1
      }
      core.process()
      assert(m1.hasBeenSent)
      assert(!m1.hasBeenReceived)
      assert(m2.hasBeenSent)
      assert(!m2.hasBeenReceived)
      assert(core.isActive)
      assert(net.isActive)
    }
  }

  simpleCoreProcessingTests(42)

  def simpleCoreProcessing3MessagesTest(time1: Int, time2: Int) =
    test(s"simple core processing, 3 messages (time1=$time1, time2=$time2)") {
      assume(time2 > time1)
      val clock = new Clock
      val net = new Network(10, 10)
      net.setClock(clock)
      val core = net.getCore(5, 5)
      val m1 = new Message(1, time1, 5, 5, 5, 6)
      val m2 = new Message(2, time2, 5, 5, 6, 5)
      val m3 = new Message(3, time1, 5, 5, 6, 6)
      assert(!net.isActive)
      assert(!core.isActive)
      net.injectMessage(m1)
      net.injectMessage(m2)
      net.injectMessage(m3)
      assert(core.isActive)
      assert(net.isActive)
      assert(!m1.hasBeenSent)
      assert(!m1.hasBeenReceived)
      assert(!m2.hasBeenSent)
      assert(!m2.hasBeenReceived)
      assert(!m3.hasBeenSent)
      assert(!m3.hasBeenReceived)
      time1 times {
        core.process()
        assert(!m1.hasBeenSent)
        assert(!m1.hasBeenReceived)
        assert(!m2.hasBeenSent)
        assert(!m2.hasBeenReceived)
        assert(!m3.hasBeenSent)
        assert(!m3.hasBeenReceived)
        clock.time += 1
      }
      core.process()
      assert(m1.hasBeenSent)
      assert(!m1.hasBeenReceived)
      assert(!m2.hasBeenSent)
      assert(!m2.hasBeenReceived)
      assert(m3.hasBeenSent)
      assert(!m3.hasBeenReceived)
      (time2 - time1) times {
        core.process()
        assert(m1.hasBeenSent)
        assert(!m1.hasBeenReceived)
        assert(!m2.hasBeenSent)
        assert(!m2.hasBeenReceived)
        assert(m3.hasBeenSent)
        assert(!m3.hasBeenReceived)
        clock.time += 1
      }
      core.process()
      assert(m1.hasBeenSent)
      assert(!m1.hasBeenReceived)
      assert(m2.hasBeenSent)
      assert(!m2.hasBeenReceived)
      assert(m3.hasBeenSent)
      assert(!m3.hasBeenReceived)
      assert(core.isActive)
      assert(net.isActive)
    }

  simpleCoreProcessing3MessagesTest(42, 43)

  test("transfer between neighboring cores [3pts]") {
    val clock = new Clock
    val net = new Network(10, 10)
    net.setClock(clock)
    val m = new Message(1, 1, 5, 9, 5, 0)
    val srcCore = net.getCore(5, 9)
    val dstCore = net.getCore(5, 0)
    val srcRouter = net.getRouter(5, 9)
    val dstRouter = net.getRouter(5, 0)
    val wire = net.getHWire(5, 9)
    assert(!net.isActive)
    assert(!srcCore.isActive)
    assert(!dstCore.isActive)
    assert(!srcRouter.isActive)
    assert(!dstRouter.isActive)
    srcCore.scheduleMessage(m)
    assert(net.isActive)
    assert(srcCore.isActive)
    assert(!dstCore.isActive)
    assert(srcRouter.isActive)
    assert(!dstRouter.isActive)
    clock.time += 1
    srcCore.process()
    assert(m.hasBeenSent)
    assert(!m.hasBeenReceived)
    assert(net.isActive)
    assert(srcCore.isActive)
    assert(!dstCore.isActive)
    assert(srcRouter.isActive)
    assert(!dstRouter.isActive)
    clock.time += 1
    srcRouter.route()
    assert(m.hasBeenSent)
    assert(!m.hasBeenReceived)
    assert(net.isActive)
    assert(!srcCore.isActive)
    assert(!dstCore.isActive)
    assert(srcRouter.isActive)
    assert(!dstRouter.isActive)
    assert( wire.transfer() )
    assert(m.hasBeenSent)
    assert(!m.hasBeenReceived)
    assert(net.isActive)
    assert(!srcCore.isActive)
    assert(!dstCore.isActive)
    assert(!srcRouter.isActive)
    assert(dstRouter.isActive)
    clock.time += 1
    dstRouter.route()
    assert(m.hasBeenSent)
    assert(m.hasBeenReceived)
    assert(!net.isActive)
    assert(!srcCore.isActive)
    assert(!dstCore.isActive)
    assert(!srcRouter.isActive)
    assert(!dstRouter.isActive)
    val received = dstCore.receivedMessages.asScala
    assert(received.size === 1)
    assert(received.head eq m)
  }
}
