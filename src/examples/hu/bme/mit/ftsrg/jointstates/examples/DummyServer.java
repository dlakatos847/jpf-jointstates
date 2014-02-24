package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer extends Thread {
  int port = -1;

  public DummyServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    System.out.println("THREAD ENTER " + this.port);
    try {
      ServerSocket serverSocket = new ServerSocket(this.port);
      Socket socket = serverSocket.accept();
      InputStream is = socket.getInputStream();
      int input = is.read();
      System.out.println("RECEIVED INPUT: " + input);
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("THREAD EXIT " + this.port);
  }

  public static void main(String[] args) {
    class DummyPrinter extends Thread {
      @Override
      public void run() {
        System.out.println("1111");
        System.out.println("2222");
        System.out.println("3333");
      }
    }

    System.out.println("STARTED MAIN");

    new DummyPrinter().start();
    new DummyServer(8080).start();
    new DummyServer(8081).start();

    System.out.println("ENDED MAIN");
  }
}
