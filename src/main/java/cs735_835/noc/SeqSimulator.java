package cs735_835.noc;

import org.apache.commons.lang3.NotImplementedException;

import java.net.URL;

/** A sequential network simulator for networks-on-chips.
 *  It is intended to be set up and run within the same thread.
 */
public class SeqSimulator extends AbstractSimulator {

  /** Builds a new simulator. */
  public SeqSimulator (Network network) {
    super(network);
    //throw new NotImplementedException("remove later");
  }

  void runSimulation() {
    var clock = new SimpleClock();
    network.setClock(clock);
    var cores = network.allCores();
    var routers = network.allRouters();
    var wires = network.allWires();
    do {
      clock.step(); // steps to 1
      for (var router : routers)
        router.route();
      for (var wire : wires)
        wire.transfer();
      for (var core : cores)
        core.process();
      afterEachStep(clock.getTime());
    } while (network.isActive());
  }

  /** Command-line application. It is called as:
   * <pre>
   * SeqSimulator &lt;width&gt; &lt;height&gt; &lt;traffic file&gt;
   * </pre>
   */
  public static void main (String[] args) throws Exception {
    int width = Integer.parseInt(args[0]);
    int height = Integer.parseInt(args[1]);
    var messages = Message.readMessagesFromURL(new URL(args[2]));
    var network = new Network(width, height);
    var sim = new SeqSimulator(network);
    messages.forEach(network::injectMessage);
    sim.timedSimulate();
  }
}
