package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class DummyClient {
  public static void main(String[] args) throws Exception {
    String hostName = "127.0.0.1";
    Socket socket = null;
    OutputStream outStream = null;
    OutputStreamWriter outWriter = null;
    try {
      InetAddress addr = InetAddress.getByName(hostName);
      System.out.println("addr: " + addr);

      int portNumber = Integer.parseInt(args[0]);
      socket = new Socket(addr, portNumber);
      System.out.println("socket: " + socket);

      outStream = socket.getOutputStream();
      System.out.println("outStream: " + outStream);

      outWriter = new OutputStreamWriter(outStream);
      System.out.println("outWriter: " + outWriter);

      outWriter.write(97);
      System.out.println("WRITE HAPPENED");
    } catch (Exception e) {
      System.out.println("EXCEPTION RECEIVED");
      e.printStackTrace();
    } finally {
      try {
        outWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
