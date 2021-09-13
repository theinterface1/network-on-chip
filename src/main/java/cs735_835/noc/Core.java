package cs735_835.noc;

import java.util.Collection;

/**
 * A network core.  Each core is both a source and a sink for messages.  Cores inject new messages
 * into the network. They also store all the messages they receive for later analysis. Conceptually,
 * each core maintains a queue of messages generated that are waiting to be sent.  This queue is
 * used by the router associated with this core.
 *
 * @see Network
 */
public interface Core {

  /**
   * Makes the core generate a message, potentially in the future. If the message has a scheduled
   * time in the past (or present), it will be added to the core queue at the next processing step.
   * Otherwise, it will be stored until it's ready to be added (i.e., when the network clock reaches
   * the right value).
   * <p>
   * If several messages are scheduled to be generated at the same time, they will be added to the
   * core queue in the order of their ids, <em>which may not be the order in which they were
   * injected</em> using this method.
   * </p><p>
   * Note that this method could make an inactive core active again.  Typically, though, it
   * is called to load a core <em>before</em> a simulation starts.  Method {@code
   * Network.injectMessage} uses it.</p>
   *
   * @throws IllegalArgumentException if the message's source row and
   *                                  column do not correspond to this core.
   * @see Network#injectMessage(Message)
   */
  void scheduleMessage(Message message);

  /**
   * Core activity.
   *
   * @return true if the core is active; a core is active if it has pending messages in its queue or
   * will add messages to its queue later (based on previous calls to {@code scheduleMessage}).
   * @see #scheduleMessage(Message)
   */
  boolean isActive();

  /**
   * Processes the core.  This simulates message generation.  This methods injects new messages into
   * the core queue (based on previous calls to {@code scheduleMessage}). Those messages are
   * timestamped with a sending time equal to the current network time.
   * <p>
   * Furthermore, if any of the new injected messages is marked as <em>tracked</em>, a short message
   * is displayed on {@code System.err}.</p>
   *
   * @see #scheduleMessage(Message)
   * @see Message#isTracked
   */
  void process();

  /**
   * Received messages.
   *
   * @return a collection of all the messages received by this core. It contains the messages in the
   * order in which they were received by the core.
   */
  Collection<Message> receivedMessages();
}
