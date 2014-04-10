package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Logger;

public class DummyClient implements Runnable {
  private static Logger logger = Logger.getLogger("DummyClient");
  private int port = -1;

  /*
   * messages
   */
  private int m1a = 1;
  private int m1b = 2;
  private int m2a = 3;
  private int m2b = 4;

  /**
   * @param port
   */
  public DummyClient(int port) {
    super();
    this.port = port;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    logger.info("start run");

    String hostName = "localhost";
    Socket socket = null;
    OutputStream os = null;
    try {
      InetAddress addr = InetAddress.getByName(hostName);
      socket = new Socket(addr, this.port);
      os = socket.getOutputStream();
      if (new Random(System.currentTimeMillis()).nextBoolean()) {
        logger.info("writing " + this.m1a + " to port " + this.port);
        os.write(this.m1a);
        logger.info("writing " + this.m1b + " to port " + this.port);
        os.write(this.m1b);
      } else {
        logger.info("writing " + this.m2a + " to port " + this.port);
        os.write(this.m2a);
        logger.info("writing " + this.m2b + " to port " + this.port);
        os.write(this.m2b);
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
    } finally {
      try {
        if (socket != null) {
          socket.close();
        }
      } catch (IOException e) {
        logger.severe(e.getMessage());
      }
    }

    logger.info("end run");
  }

  public static void main(String[] args) throws Exception {
    logger.info("start main");

    int port = 8080;

    if (args.length == 0) {
      args = new String[1];
      args[0] = String.valueOf(port);
    }

    for (String i : args) {
      port = Integer.parseInt(i);
      Thread t = new Thread(new DummyClient(port));
      t.start();
    }

    logger.info("end main");
  }
}
