import grading._
import org.scalatest.Suites

class SampleTests extends Suites(
  new MessageSampleTests,
  new NetworkSampleTests,
  new SeqSimulatorSampleTests,
  new NaiveExecSimulatorSampleTests(4),
  new NaiveExecSimulatorSampleTests(8),
  new ExecSimulatorSampleTests(4, 4),
  new ExecSimulatorSampleTests(4, 8),
  new NaiveExecConcurrencySampleTests(4),
  new ExecConcurrencySampleTests(4, 8)
)
