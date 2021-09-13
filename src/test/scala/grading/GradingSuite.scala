package grading

import java.util.concurrent.ThreadFactory

import edu.unh.cs.mc.grading._
import edu.unh.cs.mc.utils.threads.StoppableThread
import edu.unh.cs.mc.utils.threads.StoppableThread.Policies.{ async, waitAndStop }
import edu.unh.cs.mc.utils.threads.StoppableThread.StopPolicy
import org.scalatest.TestSuite
import org.scalatest.concurrent.ThreadSignaler
import org.scalatest.time.SpanSugar._

import scala.concurrent.ExecutionContext.Implicits.global

trait GradingSuite extends Grading
  with DualTimeLimits with NoStackOverflowError with RunnerFactory { self: TestSuite =>

  override val defaultTestSignaler = ThreadSignaler
  implicit val stopPolicy: StopPolicy = async(waitAndStop(5.0))

  val runnerFactory: ThreadFactory = new StoppableThread(_)

  implicit val tf: ThreadFactory = runnerFactory

  val shortTimeLimit = 1.second
  val longTimeLimit = 1.minute
}
