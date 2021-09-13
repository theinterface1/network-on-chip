package grading

import java.util.concurrent.TimeUnit.NANOSECONDS

import cs735_835.noc.Network

class SlowNetwork(width: Int, height: Int, delay: Double) extends Network(width, height) {

  private final val nanos: Long = (delay * 1e9).round

  override def beforeRouter(row: Int, col: Int): Unit = NANOSECONDS.sleep(nanos)
}
