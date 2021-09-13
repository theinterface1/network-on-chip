package grading

import edu.unh.cs.mc.utils.Resources
import org.scalactic.TimesOnInt._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow

import scala.jdk.CollectionConverters._

abstract class SimulatorSampleTests extends AnyFunSuite with Resources {

  import cs735_835.noc.{ Message, Network, Simulator }

  import scala.io.Source
  import scala.util.Random

  def makeSimulator(network: Network): Simulator

  def setSimulator(width: Int, height: Int, messages: Iterable[Message]): (Network, Simulator) = {
    val network = new Network(width, height)
    for (m <- messages) network.injectMessage(m)
    (network, makeSimulator(network))
  }

  def network4x4Tests(time: Int) = {
    test(s"4x4 network with 1 message (time=$time)") {
      val m = new Message(1, time, 0, 2, 2, 1)
      m.setTracked(true)
      val (_, sim) = setSimulator(4, 4, List(m))
      val r = sim.simulate().asScala
      assert(r.size === 1)
      assert(r.head eq m)
      assert(m.hasBeenSent)
      assert(m.hasBeenReceived)
      assert(m.getSendTime === time)
      assert(m.getReceiveTime === time + 6)
    }
    test(s"4x4 network with 1 message for the core (time=$time)") {
      val m = new Message(1, time, 0, 2, 0, 2)
      val (_, sim) = setSimulator(4, 4, List(m))
      val r = sim.simulate().asScala
      assert(r.size === 1)
      assert(r.head eq m)
      assert(m.hasBeenSent)
      assert(m.hasBeenReceived)
      assert(m.getSendTime === time)
      assert(m.getReceiveTime === time + 1)
    }
  }

  network4x4Tests(42)

  def variousNetworkSizesTest(width: Int, height: Int, count: Int) =
    test(s"various network sizes with 1 random message (w=$width,h=$height,c=$count)", Slow) {
      val rand = new Random(2019)
      count times {
        val time = rand.nextInt(100) + 1
        val sr = rand.nextInt(height)
        val sc = rand.nextInt(width)
        val dr = rand.nextInt(height)
        val dc = rand.nextInt(width)
        val m = new Message(1, time, sr, sc, dr, dc)
        m.setTracked(true)
        val (_, sim) = setSimulator(width, height, List(m))
        val r = sim.simulate().asScala
        assert(r.size === 1)
        assert(r.head eq m)
        assert(m.hasBeenSent)
        assert(m.hasBeenReceived)
        assert(m.getSendTime === time)
        val x = if (sc > dc) width + dc - sc else dc - sc
        val y = if (sr > dr) height + dr - sr else dr - sr
        assert(m.getReceiveTime === time + x + y + 1)
      }
    }

  variousNetworkSizesTest(13, 17, 100)

  def myTest(width: Int, height: Int, count: Int) =
    test(s"my test (w=$width,h=$height,c=$count)", Slow) {
      val rand = new Random(2019)
      count times {
        val time = rand.nextInt(100) + 1
        val sr = rand.nextInt(height)
        val sc = rand.nextInt(width)
        val dr = rand.nextInt(height)
        val dc = rand.nextInt(width)
        val m = new Message(1, time, sr, sc, dr, dc)
        val m2 = new Message( 2, time, 0,1,5,4 )
        m.setTracked(true)
        val (_, sim) = setSimulator(width, height, List(m, m2))
        val r = sim.simulate().asScala
        assert(r.size === 2)
        assert(r.head eq m)
        assert(m.hasBeenSent)
        assert(m.hasBeenReceived)
        assert(m.getSendTime === time)
        val x = if (sc > dc) width + dc - sc else dc - sc
        val y = if (sr > dr) height + dr - sr else dr - sr
        assert(m.getReceiveTime === time + x + y + 1)
      }
    }

  myTest(13, 17, 100)

  def completeSimulation(filename: String) = {

    val OutRegex = """msg.*at\s+(\p{Digit}+)""".r

    def readReceiveTimes(filename: String) = try {
      (for (line <- Source.fromResource(s"$filename.out").getLines()) yield {
        line match {
          case OutRegex(time) => time.toInt
          case _              => cancel(s"cannot run test: file $filename.out corrupted")
        }
      }).toList
    }
    catch {
      case _: NullPointerException => cancel(s"cannot run test: file $filename.out not found")
    }

    test(s"complete simulation (file=$filename)", Slow) {
      val parts = filename.split("-")
      val w = parts(0).toInt
      val h = parts(1).toInt
      val data = getResource(s"$filename.in")
      assume(data != null, s"cannot run test: file $filename.in not found")
      val in = Message.readMessagesFromURL(data).asScala
      val out = readReceiveTimes(filename)
      val (_, sim) = setSimulator(w, h, in)
      val r = sim.simulate().asScala
      for ((m, t) <- r.zip(out))
        assert(m.getReceiveTime === t, m)
    }
  }

  completeSimulation("10-10-1-10")
  completeSimulation("50-100-1-1000")

  def newTest() = {
    test("My new test of awesomenes") {
      val m1 = new Message(1, 1, 2, 0, 3, 3)
      val m2 = new Message(2, 2, 0, 3, 3, 3)
      m1.setTracked(true)
      m2.setTracked(true)
      val (_, sim) = setSimulator(4, 4, List(m1, m2))
      val r = sim.simulate().asScala
    }
  }

  newTest()
}
