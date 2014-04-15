package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer implements Runnable {
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
    System.out.println("started run");

    try {
      ServerSocket serverSocket = new ServerSocket(this.port);
      Socket socket = serverSocket.accept();
      InputStream is = socket.getInputStream();
      int m1 = is.read();
      System.out.println("read " + m1 + " on port " + this.port);
      int m2 = is.read();
      System.out.println("read " + m2 + " on port " + this.port);
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }

    System.out.println("ended run");
  }

  public static void main(String[] args) {
    System.out.println("started main");

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

    System.out.println("ended main");
  }
}
