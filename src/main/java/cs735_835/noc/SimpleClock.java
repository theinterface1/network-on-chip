package cs735_835.noc;

/** A simple clock.  This implementation is not thread-safe.
 */
public class SimpleClock implements Clock {

  private int time;

  public int getTime () {
    return time;
  }

  /** Step the clock forward. */
  public void step() {
    time += 1;
  }
}
