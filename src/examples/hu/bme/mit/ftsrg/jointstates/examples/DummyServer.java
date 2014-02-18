package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer extends Thread {
  // @Override
  // public void run() {
  // System.out.println("1111");
  // System.out.println("2222");
  // System.out.println("3333");
  // System.out.println("THREAD EXIT");
  // }

  public static void main(String[] args) {
    try {
      // new DummyServer().start();
      ServerSocket serverSocket = new ServerSocket(8080);
      Socket socket = serverSocket.accept();
      InputStream is = socket.getInputStream();
      int input = is.read();
      System.out.println(input);
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
