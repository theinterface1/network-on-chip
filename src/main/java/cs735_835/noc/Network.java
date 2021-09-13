package cs735_835.noc;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** A grid network.  Networks consist of cores, routers and wires.  The grid is 4-regular (each
 * router has 4 neighbors) with a torus topology (the right edge of the grid connects back to the
 * left edge; the bottom of the grid connects back to the top).  Each router is associated with a
 * core that acts as both a source and a destination (it sends and receives messages).  Each router
 * (and associated core) is identified by a row and column in the grid.
 *
 * <p>Wires are unidirectional.  Horizontal wires transport data from left to right; vertical wires
 * transport data from top to bottom. Thus, each router has 2 incoming wires (North and West) and
 * two outgoing wires (East and South).</p>
 *
 * <p>The network, its cores, routers and wires are thread-safe in a limited way.  It makes little
 * sense for threads to process routers and wires or routers and cores concurrently as the results
 * would be unpredictable.  It is intended that proper synchronization is used to ensure 1) that all
 * routers are processed before wires and cores and processes and 2) that all cores and wires are
 * processed before routers are processed again.  Cores and wires can be processed concurrently.</p>
 *
 * <p>
 * A typical sequential simulator would look like this:
 * <pre>{@code
 *   Network network = new Network(...);
 *   network.setClock(clock);
 *   network.injectMessages(...);
 *   List<Core> cores = network.allCores();
 *   List<Router> routers = network.allRouters();
 *   List<Wire> wires = network.allWires();
 *   do {
 *     clock.step();
 *     for (Router router : routers)
 *       router.route();
 *     for (Wire wire : wires)
 *       wire.transfer();
 *     for (Core core : cores)
 *       core.process();
 *     afterEachStep(clock.getTime());
 *   } while (network.isActive());
 * }</pre>
 * </p>
 *
 * @see Router
 * @see Core
 * @see Wire
 */
public class Network {
  ArrayList<MyRouter> routers;
  ArrayList<MyCore> cores;
  ArrayList<MyWire> wires;
  Clock clock;


  /**
   * Network dimensions.
   */
  public final int width, height;

  /**
   * Builds a network.
   */
  public Network(int w, int h) {
    width = w;
    height = h;
    routers = new ArrayList<MyRouter>(w*h);
    cores = new ArrayList<MyCore>(w*h);
    wires = new ArrayList<MyWire>(2*w*h);
    for( int i=0; i < w*h; i++ ){
      cores.add( new MyCore(this) );
      routers.add( new MyRouter(i/w, i%w, this));
      cores.get(i).setRouter( routers.get(i) );
    }
    for( int i=0; i < w*h; i++ ) {
      //create wires from west to east
      wires.add(new MyWire(routers.get(i).east, routers.get( (i/w)*w + (i+1)%w ).west));
      //create wires from north to south
      wires.add(new MyWire(routers.get(i).south, routers.get( ((i/w)+1)%h * w + i%w ).north));
    }

  }

  /**
   * Sets the network clock.
   */
  public void setClock(Clock c) {
    clock = c;
    for( int i=0; i < cores.size(); i++ ){
      cores.get(i).network = this;
      routers.get(i).network = this;
    }
  }

  public Clock getClock(){
    return clock;
  }

  /**
   * Network activity.  This method is thread-safe.  However, it does not attempt to build a
   * consistent snapshot of the network.  As such, it is unreliable if threads are currently running
   * the network.  For instance, this method can return false on an active network if it checks a
   * core <em>after</em> a message is loaded into a router (the message has left the core) but
   * checks the corresponding router <em>before</em> it gets the message.  The best way to use this
   * method is to call it only when all the threads running the network are stopped, for instance at
   * a synchronization barrier.
   *
   * @return true if the network is active; a network is active if at least one of its routers is
   * active.
   * @see Router#isActive
   */
  public boolean isActive() {
    for ( Router r : routers )
      if( r.isActive() )
        return true;
    return false;
  }

  /**
   * Returns the router at the specified location in the grid.
   *
   * @throws IndexOutOfBoundsException
   */
  public Router getRouter(int row, int col) {
    int index = row * width + col;
    if( index > routers.size() )
      throw new IndexOutOfBoundsException();
    return routers.get( row * width + col );
  }

  /**
   * Returns the South wire of the router at the specified location in the grid.
   *
   * @throws IndexOutOfBoundsException
   */
  @SuppressWarnings("unused")
  public Wire getVWire(int row, int col) {
    int index = (row * width + col) * 2 + 1;
    if( index > wires.size() )
      throw new IndexOutOfBoundsException();
    return wires.get( (row * width + col) * 2 + 1 );
  }

  /**
   * Returns the East wire of the router at the specified location in the grid.
   *
   * @throws IndexOutOfBoundsException
   */
  public Wire getHWire(int row, int col) {
    return wires.get( (row * width + col) * 2 );
  }

  /**
   * Returns the core at the specified location in the grid.
   *
   * @throws IndexOutOfBoundsException
   */
  public Core getCore(int row, int col) {
    return cores.get(row*width + col);
  }

  /**
   * Returns all the routers in the network as an unmodifiable list. The order in which routers are
   * returned is not specified. The list is safe for use from multiple threads.
   */
  public List<Router> allRouters() {
    return Collections.unmodifiableList( routers );
  }

  /**
   * Returns all the wires in the network as an unmodifiable list. The order in which wires are
   * returned is not specified. The list is safe for use from multiple threads.
   */
  public List<Wire> allWires() {
    return Collections.unmodifiableList( wires );
  }

  /**
   * Returns all the cores in the network as an unmodifiable list. The order in which cores are
   * returned is not specified. The list is safe for use from multiple threads.
   */
  public List<Core> allCores() {
    return Collections.unmodifiableList( cores );
  }

  /**
   * Makes the network generate a message, potentially in the future. This is achieved my calling
   * {@code scheduleMessage} on the corresponding core.
   *
   * @throws IllegalArgumentException if the message's source row and column do not correspond to
   *                                  any core.
   * @see Core#scheduleMessage(Message)
   */
  public void injectMessage(Message msg) {
    try {
      getCore(msg.sourceRow(), msg.sourceCol()).scheduleMessage(msg);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException
          (String.format("no such core: (%d, %d)", msg.sourceRow(), msg.sourceCol()));
    }
  }

  /**
   * Hook called as the first step of {@code route} in routers.  Does nothing by default but can
   * be overridden.
   */
  protected void beforeRouter(int row, int col) {
  }
}
