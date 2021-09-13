package grading

import org.scalatest.time.SpanSugar._

class ExecSimulatorTests(val workers: Int, val tasks: Int)
  extends SimulatorTests with ExecSimulatorSetup with GradingSuite {

  override val longTimeLimit = 10.seconds
}
