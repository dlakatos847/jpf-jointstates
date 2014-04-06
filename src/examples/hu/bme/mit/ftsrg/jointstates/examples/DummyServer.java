package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer implements Runnable {
  private int port = -1;
  private static Object sync = new Object();

  public DummyServer() {
    this.port = 8080;
  }

  public DummyServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    // System.out.println("THREAD ENTER " + this.port);
    try {
      ServerSocket serverSocket = new ServerSocket(this.port);
      Socket socket = serverSocket.accept();
      InputStream is = socket.getInputStream();
      int input = is.read();
      System.out.println("RECEIVED INPUT " + input + " on port " + this.port);
      if (input == 99) {
        synchronized (sync) {
          sync.wait();
        }
      }
      socket.close();
      serverSocket.close();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    // System.out.println("THREAD EXIT " + this.port);
  }

  public static void main(String[] args) {
    Thread t = new Thread(new DummyServer());
    t.start();
  }
}
