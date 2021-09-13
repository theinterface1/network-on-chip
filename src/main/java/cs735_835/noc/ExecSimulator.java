package cs735_835.noc;

import org.apache.commons.lang3.NotImplementedException;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * A parallel simulator for networks-on-chips.
 */
public class ExecSimulator extends AbstractSimulator {
  ExecutorService service;
  int nbTasks;


  /**
   * Builds a new simulator.
   */
  public ExecSimulator(Network network, ExecutorService exec, int nbTasks) {
    super(network);
    service = exec;
    this.nbTasks = nbTasks;
  }

  /**
   * Command-line application. It is called as:
   * <pre>
   * ExecSimulator &lt;width&gt; &lt;height&gt; &lt;traffic file&gt; &lt;#threads&gt; &lt;granularity&gt;
   * </pre>
   */
  public static void main(String[] args) throws Exception {
    int width = Integer.parseInt(args[0]);
    int height = Integer.parseInt(args[1]);
    int nbWorkers = Integer.parseInt(args[3]);
    int nbTasks = Integer.parseInt(args[4]);
    var messages = Message.readMessagesFromURL(new URL(args[2]));
    var network = new Network(width, height);
    var exec = Executors.newFixedThreadPool(nbWorkers);
    var sim = new ExecSimulator(network, exec, nbTasks);
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
    int networkSize = routers.size();
    int over = networkSize / nbTasks;
    if( networkSize % nbTasks != 0 )
      over++;
    Semaphore sem = new Semaphore( 0 );
    do{
      clock.step();
      //-----------------------Routers---------------------
      for( int i=0; i<nbTasks; i++ ){
        List<Router> routerList;
        try {
          routerList = routers.subList(i * over, (i + 1) * over);
        }catch ( IndexOutOfBoundsException e){
          routerList = routers.subList(i * over, routers.size());
        }

        List<Router> finalRouterList = routerList;
        service.execute(() -> {
          for(Router r : finalRouterList){
            r.route();
            sem.release();
          }
        });
      }

      try {
        //Thread.sleep(100);
        //System.out.println(sem.availablePermits());
        sem.acquire(networkSize);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      //----------------------------------------------------
      for( int i=0; i<nbTasks; i++ ){
        List<Core> coreList;
        try {
          coreList = cores.subList(i*over, (i+1)*over );
        }catch ( IndexOutOfBoundsException e){
          coreList = cores.subList(i*over, cores.size() );
        }

        List<Core> finalCoreList = coreList;
        service.submit(() -> {
          for(Core c : finalCoreList){
            c.process();
            sem.release();
          }
        });
      }
      for( int i=0; i < 2*nbTasks; i++ ){
        List<Wire> wireList;
        try {
          wireList = wires.subList(i * over, (i + 1) * over);
        } catch (IndexOutOfBoundsException e){
          wireList = wires.subList(i * over, wires.size());
        }
        List<Wire> finalWireList = wireList;
        service.submit(() -> {
          for( Wire w : finalWireList){
            w.transfer();
            sem.release();
          }
        });
      }
      try {
        sem.acquire(networkSize*3 );
      } catch (InterruptedException e) {
        e.printStackTrace();
      }


    }while( network.isActive() );
  }
}
