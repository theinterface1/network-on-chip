package grading

import edu.unh.cs.mc.grading.Grading
import org.scalatest.Suites

class NaiveExecSimulatorSuite extends Suites(
  new NaiveExecSimulatorTests(1),
  new NaiveExecSimulatorTests(2),
  new NaiveExecSimulatorTests(4),
  new NaiveExecSimulatorTests(8),
  new NaiveExecSimulatorTests(16)
) with Grading
