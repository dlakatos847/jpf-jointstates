package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class DummyClient implements Runnable {
  private int port = -1;

  /*
   * messages
   */
  private int w1 = 1;
  private int r1 = 0;
  private int r1e = 11;
  private int w2 = 2;
  private int r2 = 0;
  private int r2e = 21;

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
    InputStream is = null;
    OutputStream os = null;
    try {
      InetAddress addr = InetAddress.getByName(hostName);
      socket = new Socket(addr, this.port);
      is = socket.getInputStream();
      os = socket.getOutputStream();
      if (new Random(System.currentTimeMillis()).nextBoolean()) {
        System.out.println("write " + this.w1 + " to port " + this.port);
        os.write(this.w1);
        System.out.println("wrote " + this.w1 + " to port " + this.port);

        System.out.println("read from port " + this.port);
        this.r1 = is.read();
        System.out.println("read " + this.r1 + " from port " + this.port);
        evaluate(this.r1, this.r1e);
      } else {
        System.out.println("write " + this.w2 + " to port " + this.port);
        os.write(this.w2);
        System.out.println("wrote " + this.w2 + " to port " + this.port);

        System.out.println("read from port " + this.port);
        this.r2 = is.read();
        System.out.println("read " + this.r2 + " from port " + this.port);
        evaluate(this.r2, this.r2e);
      }
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

  private void evaluate(int received, int expected) {
    System.out.print("result: ");
    if (received == expected) {
      System.out.println("expected");
    } else {
      System.out.println("unexpected");
    }
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
