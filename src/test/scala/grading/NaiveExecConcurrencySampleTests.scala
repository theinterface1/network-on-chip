package grading

class NaiveExecConcurrencySampleTests(workers: Int)
  extends ConcurrencyTests(workers) with NaiveExecSimulatorSetup {
  concurrencyTest(4, 4, 2, 3)
}
