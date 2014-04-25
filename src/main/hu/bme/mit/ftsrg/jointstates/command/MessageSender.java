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
  protected static Logger logger;

  Map<Side, Integer> messagePorts = new HashMap<Side, Integer>();
  BlockingQueue<Message> outboundQueue = new LinkedBlockingQueue<Message>();

  public MessageSender() {
    this.messagePorts.put(Side.COMMANDER, 62301);
    this.messagePorts.put(Side.CLIENT, 62302);
    this.messagePorts.put(Side.SERVER, 62303);
    MessageSender.logger = Logger.getLogger(MessageSender.class.getCanonicalName());
  }

  public MessageSender(Logger logger) {
    this.messagePorts.put(Side.COMMANDER, 62301);
    this.messagePorts.put(Side.CLIENT, 62302);
    this.messagePorts.put(Side.SERVER, 62303);
    MessageSender.logger = logger;
  }

  public void sendMessage(Message msg) throws InterruptedException {
    this.outboundQueue.put(msg);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        Message msg = this.outboundQueue.take();
        int destinationPort = this.messagePorts.get(msg.getDestination());
        Socket s = new Socket(Inet4Address.getByName("localhost"), destinationPort);
        OutputStream os = s.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(msg);
        logger.warning("jointstates message sent from: " + msg.getSource() + " to: " + msg.getDestination() + " message: " + msg.getMsgType());
      } catch (IOException | InterruptedException e) {
        logger.severe(e.getMessage());
      }
    }
  }
}