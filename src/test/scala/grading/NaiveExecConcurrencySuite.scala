package grading

import edu.unh.cs.mc.grading.Grading
import org.scalatest.Suites

class NaiveExecConcurrencySuite extends Suites(
  new NaiveExecConcurrencyTests(2),
  new NaiveExecConcurrencyTests(4),
  new NaiveExecConcurrencyTests(6)
) with Grading
