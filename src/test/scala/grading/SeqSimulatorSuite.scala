package grading

import cs735_835.noc.{ Network, Simulator }
import org.scalatest.time.SpanSugar._

class SeqSimulatorSuite extends SimulatorTests with GradingSuite {

  override val longTimeLimit = 10.seconds

  def makeSimulator(network: Network): Simulator = new cs735_835.noc.SeqSimulator(network)
}
