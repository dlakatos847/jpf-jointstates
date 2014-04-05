package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer implements Runnable {
  int port = -1;

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
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // System.out.println("THREAD EXIT " + this.port);
  }

  public static void main(String[] args) {
    // class DummyPrinter extends Thread {
    // @Override
    // public void run() {
    // System.out.println("1111");
    // System.out.println("2222");
    // System.out.println("3333");
    // }
    // }

    for (String input : args) {
      int port = Integer.parseInt(input);
      Thread t = new Thread(new DummyServer(port));
      t.start();
    }
    for (String input : args) {
      int port = Integer.parseInt(input);
      Thread t = new Thread(new DummyServer(port));
      t.start();
    }
  }
}
