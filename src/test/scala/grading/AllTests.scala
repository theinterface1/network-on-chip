package grading

import org.scalatest.Suites

class AllTests extends Suites(
  new MessageSuite,
  new NetworkSuite,
  new SeqSimulatorSuite,
  new NaiveExecSimulatorSuite,
  new ExecSimulatorSuite,
  new NaiveExecConcurrencySuite,
  new ExecConcurrencySuite
)
