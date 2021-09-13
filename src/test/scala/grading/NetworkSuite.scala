package grading

import java.util.concurrent.atomic.AtomicInteger

import cs735_835.noc._
import org.scalactic.TimesOnInt._

class NetworkSuite extends NetworkSampleTests with GradingSuite {

  import scala.jdk.CollectionConverters._

  for (time <- List(1, 2019))
    simpleCoreProcessingTests(time)

  for ((time1, time2) <- List((1, 5), (2019, 6102)))
    simpleCoreProcessing3MessagesTest(time1, time2)

  test("running an empty network") {
    val clock = new Clock
    val net = new Network(10, 10)
    net.setClock(clock)
    val cores = net.allCores
    val routers = net.allRouters
    val wires = net.allWires
    1000 times {
      clock.time += 1
      routers.forEach(router => router.route())
      wires.forEach(wire => wire.transfer())
      cores.forEach(core => core.process())
      assert(!net.isActive)
    }
  }

  test("message scheduled in the past is sent immediately [2pts]") {
    val clock = new Clock
    val net = new Network(10, 10)
    net.setClock(clock)
    val core = net.getCore(5, 5)
    val m = new Message(1, 10, 5, 5, 5, 6)
    clock.time = 100
    net.injectMessage(m)
    core.process()
    assert(m.hasBeenSent)
    assert(!m.hasBeenReceived)
    assert(m.getSendTime === 100)
  }

  test("core sends message to itself [2pts]") {
    val hookRun = new AtomicInteger
    val clock = new Clock
    val net = new Network(10, 10) {
      override protected def beforeRouter(row: Int, col: Int) = hookRun.incrementAndGet
    }
    net.setClock(clock)
    val core = net.getCore(5, 5)
    val router = net.getRouter(5, 5)
    val m = new Message(1, 1, 5, 5, 5, 5)
    net.injectMessage(m)
    clock.time += 1
    core.process()
    assert(m.hasBeenSent)
    assert(!m.hasBeenReceived)
    clock.time += 1
    router.route()
    assert(hookRun.get === 1)
    assert(m.hasBeenSent)
    assert(m.hasBeenReceived)
    assert(m.getSendTime === 1)
    assert(m.getReceiveTime === 2)
    val received = core.receivedMessages.asScala
    assert(received.size === 1)
    assert(received.head eq m)
  }
}
