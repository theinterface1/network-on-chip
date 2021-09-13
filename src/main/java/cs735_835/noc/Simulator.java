package cs735_835.noc;

import java.util.List;

/**
 * Network-on-chip simulators.
 */
public interface Simulator {

  /**
   * Runs the simulation.
   *
   * @return a list of all received messages, in order of their ids.
   */
  List<Message> simulate();
}
