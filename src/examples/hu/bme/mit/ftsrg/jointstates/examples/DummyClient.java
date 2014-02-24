package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DummyClient {
  public static void main(String[] args) throws Exception {
    System.out.println("STARTED MAIN");
    String hostName = "127.0.0.1";
    Socket socket = null;
    OutputStream outStream = null;
    try {
      InetAddress addr = InetAddress.getByName(hostName);
      int portNumber = Integer.parseInt(args[0]);
      socket = new Socket(addr, portNumber);
      outStream = socket.getOutputStream();
      System.out.println("WRITING");
      outStream.write(97);
    } catch (Exception e) {
      System.out.println("EXCEPTION RECEIVED");
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println("ENDED MAIN");
  }
}
