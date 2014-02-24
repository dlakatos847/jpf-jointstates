package hu.bme.mit.ftsrg.jointstates;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HeartbeatResponder extends Thread {
  // private static Logger logger =
  // JPF.getLogger(HeartbeatResponder.class.getName());

  private final int port;
  private final int expectedInput = 99;
  private final int output = 100;
  private boolean running = true;
  ServerSocket serverSocket;

  public HeartbeatResponder(int port) {
    setDaemon(false);
    this.port = port;
  }

  @Override
  public void run() {
    try {
      this.serverSocket = new ServerSocket(this.port);
      while (this.running) {
        Socket socket = this.serverSocket.accept();
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        int input = is.read();
        System.out.println("HEARTBEAT REQUEST RECEIVED: " + input);
        if (input == this.expectedInput) {
          os.write(this.output);
        }
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    this.serverSocket.close();
    this.running = false;
    super.finalize();
  }
}
