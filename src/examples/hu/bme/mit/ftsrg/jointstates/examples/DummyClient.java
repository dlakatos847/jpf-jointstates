package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DummyClient implements Runnable {
  private int port;

  public DummyClient(int port) {
    this.port = port;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    String hostName = "127.0.0.1";
    Socket socket = null;
    OutputStream outStream = null;
    try {
      InetAddress addr = InetAddress.getByName(hostName);
      socket = new Socket(addr, this.port);
      outStream = socket.getOutputStream();
      System.out.println("WRITING " + 99 + " to port " + this.port);
      outStream.write(99);
      System.out.println("WRITING " + 100 + " to port " + this.port);
      outStream.write(100);
    } catch (Exception e) {
      System.out.println("EXCEPTION RECEIVED");
      e.printStackTrace();
    } finally {
      try {
        if (socket != null) {
          socket.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("STARTED MAIN");
    for (String i : args) {
      int port = Integer.parseInt(i);
      Thread t = new Thread(new DummyClient(port));
      t.start();
    }
    System.out.println("ENDED MAIN");
  }
}
