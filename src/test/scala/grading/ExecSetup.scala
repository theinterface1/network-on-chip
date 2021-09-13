package grading

import org.scalatest.{ BeforeAndAfter, Suite, SuiteMixin }

trait ExecSetup extends SuiteMixin with BeforeAndAfter { self: Suite =>

  import java.util.concurrent.TimeUnit.SECONDS
  import java.util.concurrent.{ ExecutorService, Executors }

  val workers: Int

  protected var exec: ExecutorService = _

  before {
    exec = Executors.newFixedThreadPool(workers)
  }

  after {
    assert(!exec.isShutdown)
    exec.shutdown()
    if (!exec.awaitTermination(1, SECONDS)) {
      System.err.println("SHUTTING DOWN EXECUTOR!")
      exec.shutdownNow()
    }
  }
}
