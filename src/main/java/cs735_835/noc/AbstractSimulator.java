package cs735_835.noc;

import java.util.List;

/**
 * Base class for the sequential and parallel simulators.
 */
abstract class AbstractSimulator implements Simulator {

  /**
   * The network that is being simulated.
   */
  protected final Network network;

  /**
   * Builds a new simulator.
   */
  public AbstractSimulator(Network network) {
    this.network = network;
  }

  /** Runs the simulation. */
  abstract void runSimulation();

  /** Hook executed after each simulation step. Empty by default, but can be overridden. */
  protected void afterEachStep(int time) {
  }

  /**
   * Runs the simulation.
   *
   * @return a list of all received messages, in order of their ids.
   */
  public List<Message> simulate() {
    runSimulation();
    var received = new java.util.ArrayList<Message>();
    for (int r = 0, h = network.height; r < h; r++) {
      for (int c = 0, w = network.width; c < w; c++) {
        for (var message : network.getCore(r, c).receivedMessages()) {
          assert message.hasBeenReceived() : message;
          assert message.destRow() == r && message.destCol() == c : message;
          received.add(message);
        }
      }
    }
    java.util.Collections.sort(received);
    return received;
  }

  /**
   * Runs the simulation and displays results.
   */
  void timedSimulate() {
    long time = System.nanoTime();
    List<Message> received = simulate();
    time = System.nanoTime() - time;
    System.err.printf("simulation completed: %.2f seconds%n", time / 1e9);
    received.forEach(System.out::println);
  }
}
