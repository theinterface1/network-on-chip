package grading

object Grade extends edu.unh.cs.mc.grading.GraderApp(
  10 -> new MessageSuite,
  10 -> new NetworkSuite,
  10 -> new SeqSimulatorSuite,
  20 -> new NaiveExecSimulatorSuite,
  20 -> new ExecSimulatorSuite,
  5 -> new NaiveExecConcurrencySuite,
  5 -> new ExecConcurrencySuite
)
