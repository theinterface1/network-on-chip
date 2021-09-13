package grading

import cs735_835.noc.{ Network, Simulator }

class SeqSimulatorSampleTests extends SimulatorSampleTests {
  def makeSimulator(network: Network): Simulator = new cs735_835.noc.SeqSimulator(network)
}
