package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LuckyServer implements Runnable {
  private int port = -1;

  /**
   * @param port
   */
  public LuckyServer(int port) {
    super();
    this.port = port;
  }

  @Override
  public void run() {
    System.out.println("start run");

    try {
      ServerSocket serverSocket = new ServerSocket(this.port);
      Socket socket = serverSocket.accept();
      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();

      System.out.println("read from port " + this.port);
      int r = is.read();
      System.out.println("read " + r + " from port " + this.port);

      int w;
      w = 10 * r;

      System.out.println("write " + w + " to port " + this.port);
      os.write(w);
      System.out.println("wrote " + w + " to port " + this.port);

      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }

    System.out.println("end run");
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
      Thread t = new Thread(new LuckyServer(port));
      t.start();
    }

    System.out.println("ended main");
  }
}
