package hu.bme.mit.ftsrg.jointstates.command;

import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class MessageSender implements Runnable {
  protected static final Logger logger = Logger.getLogger(MessageSender.class.getCanonicalName());

  Map<Side, Integer> messagePorts = new HashMap<Side, Integer>();
  BlockingQueue<Message> outboundQueue = new LinkedBlockingQueue<Message>();
  BlockingQueue<Side> outboundDestination = new LinkedBlockingQueue<Side>();

  public MessageSender() {
    this.messagePorts.put(Side.COMMANDER, 62301);
    this.messagePorts.put(Side.CLIENT, 62302);
    this.messagePorts.put(Side.SERVER, 62303);
  }

  public void sendMessage(Side side, Message msg) throws InterruptedException {
    this.outboundQueue.put(msg);
    this.outboundDestination.put(side);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        int destinationPort = this.messagePorts.get(this.outboundDestination.take());
        Socket s = new Socket(Inet4Address.getByName("localhost"), destinationPort);
        OutputStream os = s.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(this.outboundQueue.take());
      } catch (IOException e) {
        logger.severe(e.getMessage());
      } catch (InterruptedException e) {
        logger.fine(e.getMessage());
      }
    }
  }
}