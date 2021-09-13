package grading

class ExecConcurrencySampleTests(workers: Int, val tasks: Int)
  extends ConcurrencyTests(workers) with ExecSimulatorSetup {
  concurrencyTest(4, 4, 2, 3)
}
