package grading

import org.scalatest.time.SpanSugar._

class NaiveExecConcurrencyTests(workers: Int)
  extends NaiveExecConcurrencySampleTests(workers) with GradingSuite {

  override val longTimeLimit = 45.seconds

  concurrencyTest(4, 6, 2, 1)
}
