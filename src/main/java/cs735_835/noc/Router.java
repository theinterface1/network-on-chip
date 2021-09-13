package cs735_835.noc;

/** A network router.  Each router is associated with 2 incoming wires, 2 outgoing wires and a core.
 * It routes messages from core to wires, from wire to core and from wire to wire.  A router
 * maintains 4 ports (one for each wire) that contain at most one (incoming or outgoing) message
 * each.  See handout for details on the routing algorithm.
 *
 * @see Network
 */
public interface Router {

  /** Router activity.
   * @return true if this router is active; a router is active if it contains at least one message
   * in its ports or its core is active.
   */
  boolean isActive ();

  /** Routes messages.
   * <p>If a "tracked" message is processed, some explanation is displayed on {@code System.err}.</p>
   */
  void route ();
}
