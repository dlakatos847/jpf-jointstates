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

  protected static final Logger logger = Logger.getLogger(MessageReceiver.class.getCanonicalName());

  int listenPort = -1;
  BlockingQueue<Message> inboundQueue = new LinkedBlockingQueue<Message>();

  public MessageReceiver(Side side) {
    Map<Side, Integer> messagePorts = new HashMap<Side, Integer>();
    messagePorts.put(Side.COMMANDER, 62301);
    messagePorts.put(Side.CLIENT, 62302);
    messagePorts.put(Side.SERVER, 62303);

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
    try {
      ss = new ServerSocket(this.listenPort);
      while (!Thread.interrupted()) {
        s = ss.accept();
        ois = new ObjectInputStream(s.getInputStream());
        this.inboundQueue.put((Message) ois.readObject());
        ois.close();
        s.close();
      }
      ss.close();
    } catch (ClassNotFoundException | IOException | InterruptedException e) {
      logger.severe(e.getMessage());
    }
  }
}