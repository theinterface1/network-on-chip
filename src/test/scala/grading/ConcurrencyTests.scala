package grading

import cs735_835.noc.{ Message, Network, Simulator }
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow

abstract class ConcurrencyTests(val workers: Int) extends AnyFunSuite {

  def makeSimulator(network: Network): Simulator

  def expectedMaxTime(steps: Int, cores: Int): Double

  def concurrencyTest(width: Int, height: Int, down: Int, right: Int) = {
    require(right < width && down < height)

    test("checks that routing steps are done in parallel when possible" +
      s" ($width,$height,$down,$right)", Slow) {
      val network = new SlowNetwork(width, height, 1.0)
      val m = new Message(1, 1, 1, 1, down, right)
      network.injectMessage(m)
      val sim = makeSimulator(network)
      val start = System.nanoTime()
      sim.simulate()
      val seconds = (System.nanoTime() - start) / 1e9
      val expected = expectedMaxTime(right + down, width * height)
      assert(
        seconds < expected + 1, // add 1 second margin
        f"simulation took too long: $seconds%.1f (expected less than $expected%.1f)")
    }
  }
}
