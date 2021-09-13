package grading

import edu.unh.cs.mc.grading.Grading
import org.scalatest.Suites

class ExecConcurrencySuite extends Suites(
  new ExecConcurrencyTests(4, 4),
  new ExecConcurrencyTests(4, 8),
  new ExecConcurrencyTests(6, 10)
) with Grading
