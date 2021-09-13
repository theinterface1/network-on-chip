package cs735_835.noc;

import com.sun.source.tree.NewArrayTree;

import java.util.*;

public class MyCore implements Core {
    LinkedList<Message> messageQueue;
    TreeSet<Message> scheduledMessages;
    volatile LinkedList<Message> receivedList;
    Network network;
    MyRouter router;
    public MyCore(Network net ){
        messageQueue = new LinkedList<Message>();
        receivedList = new LinkedList<Message>();
        scheduledMessages = new TreeSet<Message>();
    }
    public void setRouter( MyRouter r ){
        router = r;
    }
    @Override
    public void scheduleMessage(Message message) {
        scheduledMessages.add( message );
    }

    @Override
    public boolean isActive() {
        return !messageQueue.isEmpty() || !scheduledMessages.isEmpty();
    }

    @Override
    public void process() {
        int time = network.clock.getTime();

        LinkedList<Message> forDeletion = new LinkedList<Message>();
        for ( Message m : scheduledMessages ) {
            if( m.getScheduledTime() == time ){
                m.send(time);
                messageQueue.add(m);
                forDeletion.add(m);
            }
        }
        scheduledMessages.removeAll( forDeletion );
    }

    public synchronized void receiveMessage( Message m ){
        receivedList.add( m );
        m.receive( network.clock.getTime() );
    }

    @Override
    public synchronized Collection<Message> receivedMessages() {
        return receivedList;
    }
}
