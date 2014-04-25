package hu.bme.mit.ftsrg.jointstates.command;

import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class MessageReceiver implements Runnable {

  protected static Logger logger;

  int listenPort = -1;
  BlockingQueue<Message> inboundQueue = new LinkedBlockingQueue<Message>();

  public MessageReceiver(Side side) {
    Map<Side, Integer> messagePorts = new HashMap<Side, Integer>();
    messagePorts.put(Side.COMMANDER, 62301);
    messagePorts.put(Side.CLIENT, 62302);
    messagePorts.put(Side.SERVER, 62303);

    MessageReceiver.logger = Logger.getLogger(MessageReceiver.class.getCanonicalName());
    this.listenPort = messagePorts.get(side);
  }

  public MessageReceiver(Side side, Logger logger) {
    Map<Side, Integer> messagePorts = new HashMap<Side, Integer>();
    messagePorts.put(Side.COMMANDER, 62301);
    messagePorts.put(Side.CLIENT, 62302);
    messagePorts.put(Side.SERVER, 62303);

    MessageReceiver.logger = logger;
    this.listenPort = messagePorts.get(side);
  }

  public Message receiveMessage() throws InterruptedException {
    return this.inboundQueue.take();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    ServerSocket ss;
    Socket s;
    ObjectInputStream ois;
    Message msg;
    try {
      ss = new ServerSocket(this.listenPort);
      while (!Thread.interrupted()) {
        s = ss.accept();
        ois = new ObjectInputStream(s.getInputStream());
        msg = (Message) ois.readObject();
        logger.warning("jointstates message received from: " + msg.getSource() + " to: " + msg.getDestination() + " message: " + msg.getMsgType());
        this.inboundQueue.put(msg);
        ois.close();
        s.close();
      }
      ss.close();
    } catch (ClassNotFoundException | IOException | InterruptedException e) {
      logger.severe(e.getMessage());
    }
  }
}