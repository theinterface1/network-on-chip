package grading

import edu.unh.cs.mc.grading.Grading
import org.scalatest.Suites

class ExecSimulatorSuite extends Suites(
  new ExecSimulatorTests(1, 1),
  new ExecSimulatorTests(2, 2),
  new ExecSimulatorTests(2, 4),
  new ExecSimulatorTests(2, 10),
  new ExecSimulatorTests(4, 4),
  new ExecSimulatorTests(4, 8),
  new ExecSimulatorTests(4, 20),
  new ExecSimulatorTests(3, 10)
) with Grading
