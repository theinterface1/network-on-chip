package grading

import cs735_835.noc.{ Network, Simulator }
import org.scalatest.Suite

trait NaiveExecSimulatorSetup extends ExecSetup { self: Suite =>

  abstract override def suiteName: String = s"${super.suiteName}(workers=$workers)"

  def expectedMaxTime(steps: Int, cores: Int): Double = (cores.toDouble / workers).ceil * steps

  def makeSimulator(network: Network): Simulator =
    new cs735_835.noc.NaiveExecSimulator(network, exec)
}
