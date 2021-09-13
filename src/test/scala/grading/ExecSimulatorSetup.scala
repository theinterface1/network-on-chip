package grading

import cs735_835.noc.{ Network, Simulator }
import org.scalatest.Suite

trait ExecSimulatorSetup extends ExecSetup { self: Suite =>

  val tasks: Int

  abstract override def suiteName: String = s"${super.suiteName}(workers=$workers,tasks=$tasks)"

  def expectedMaxTime(steps: Int, cores: Int): Double =
    if (cores < tasks) (cores.toDouble / workers).ceil * steps
    else (cores.toDouble / tasks).ceil * (tasks.toDouble / workers).ceil * steps

  def makeSimulator(network: Network): Simulator =
    new cs735_835.noc.ExecSimulator(network, exec, tasks)
}
