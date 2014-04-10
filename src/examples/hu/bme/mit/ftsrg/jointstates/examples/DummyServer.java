package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class DummyServer implements Runnable {
  private static Logger logger = Logger.getLogger("DummyServer");
  private int port = -1;

  /**
   * @param port
   */
  public DummyServer(int port) {
    super();
    this.port = port;
  }

  @Override
  public void run() {
    logger.info("started run");

    try {
      ServerSocket serverSocket = new ServerSocket(this.port);
      Socket socket = serverSocket.accept();
      InputStream is = socket.getInputStream();
      int m1 = is.read();
      logger.info("received " + m1 + " from port " + this.port);
      int m2 = is.read();
      logger.info("received " + m2 + " from port " + this.port);
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      logger.severe(e.getMessage());
    }

    logger.info("ended run");
  }

  public static void main(String[] args) {
    logger.info("started main");

    int port = 8080;

    if (args.length == 0) {
      args = new String[1];
      args[0] = String.valueOf(port);
    }

    for (String i : args) {
      port = Integer.parseInt(i);
      Thread t = new Thread(new DummyServer(port));
      t.start();
    }

    logger.info("ended main");
  }
}
