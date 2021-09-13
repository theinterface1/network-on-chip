package grading

import org.scalatest.time.SpanSugar._

class NaiveExecSimulatorTests(val workers: Int)
  extends SimulatorTests with NaiveExecSimulatorSetup with GradingSuite {

  override val longTimeLimit = 30.seconds
}
