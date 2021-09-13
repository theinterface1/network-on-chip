package cs735_835.noc;

import javafx.concurrent.Task;
import org.apache.commons.lang3.NotImplementedException;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * A parallel simulator for networks-on-chips.
 */
public class NaiveExecSimulator extends AbstractSimulator {
  ExecutorService service;

  /**
   * Builds a new simulator.
   */
  public NaiveExecSimulator(Network network, ExecutorService exec) {
    super(network);
    service = exec;
    //throw new NotImplementedException("Remove Later");
  }

  /**
   * Command-line application. It is called as:
   * <pre>
   * NaiveExecSimulator &lt;width&gt; &lt;height&gt; &lt;traffic file&gt; &lt;#threads&gt;
   * </pre>
   */
  public static void main(String[] args) throws Exception {
    int width = Integer.parseInt(args[0]);
    int height = Integer.parseInt(args[1]);
    int nbWorkers = Integer.parseInt(args[3]);
    var messages = Message.readMessagesFromURL(new URL(args[2]));
    var network = new Network(width, height);
    var exec = Executors.newFixedThreadPool(nbWorkers);
    var sim = new NaiveExecSimulator(network, exec);
    messages.forEach(network::injectMessage);
    sim.timedSimulate();
    exec.shutdown();
  }

  void runSimulation() {
    var clock = new SimpleClock();
    network.setClock(clock);
    var cores = network.allCores();
    var routers = network.allRouters();
    var wires = network.allWires();
    do{
      clock.step();
      Semaphore sem = new Semaphore( 0 );
      for( Router r : routers ){
        service.submit(() -> {
          r.route();
          sem.release();
        });
      }
      try {
        sem.acquire( routers.size() );
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      //---------------------------
      for( Wire w : wires ){
        service.submit(() -> {
          w.transfer();
          sem.release();
        });
      }
      for( Core c : cores ){
        service.submit(() -> {
          c.process();
          sem.release();
        });
      }
      try {
        sem.acquire(wires.size() + cores.size() );
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }while( network.isActive() );
  }
}
