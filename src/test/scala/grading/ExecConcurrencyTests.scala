package grading

import org.scalatest.time.SpanSugar._

class ExecConcurrencyTests(workers: Int, tasks: Int)
  extends ExecConcurrencySampleTests(workers, tasks) with GradingSuite {

  override val longTimeLimit = 25.seconds

  concurrencyTest(4, 6, 2, 1)
}
