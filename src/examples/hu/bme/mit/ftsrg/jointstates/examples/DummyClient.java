package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DummyClient implements Runnable {
  private int port = -1;

  /*
   * messages
   */
  private int m1a = 1;
  private int m1b = 11;
  private int m2a = 2;
  private int m2b = 21;

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
    System.out.println("start run");

    String hostName = "localhost";
    Socket socket = null;
    OutputStream os = null;
    try {
      InetAddress addr = InetAddress.getByName(hostName);
      socket = new Socket(addr, this.port);
      os = socket.getOutputStream();
      // if (new Random(System.currentTimeMillis()).nextBoolean()) {
      System.out.println("writing " + this.m1a + " to port " + this.port);
      os.write(this.m1a);
      System.out.println("writing " + this.m1b + " to port " + this.port);
      os.write(this.m1b);
      // } else {
      // System.out.println("writing " + this.m2a + " to port " + this.port);
      // os.write(this.m2a);
      // System.out.println("writing " + this.m2b + " to port " + this.port);
      // os.write(this.m2b);
      // }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    } finally {
      try {
        if (socket != null) {
          socket.close();
        }
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    }

    System.out.println("end run");
  }

  public static void main(String[] args) throws Exception {
    System.out.println("start main");

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

    System.out.println("end main");
  }
}
