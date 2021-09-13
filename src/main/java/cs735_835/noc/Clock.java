package cs735_835.noc;

/** A clock.  Like a regular wall clock, all one can do from this interface is look at it.
 * Note, however, that clock implementations may or may not be thread safe.  In particular, some
 * clocks may offer partial safety only (e.g., multiple readers but single writer) or no thread
 * safety at all.
 */
public interface Clock {

  /** Clock time. */
  int getTime();
}
