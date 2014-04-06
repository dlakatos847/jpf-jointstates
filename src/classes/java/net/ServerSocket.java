package java.net;

import java.io.Closeable;
import java.io.IOException;

public class ServerSocket implements Closeable {
  private static int seq_serverSocketId = 0;

  private int serverSocketId = -1;
  private int port = -1;
  private boolean isClosed = false;

  public ServerSocket(int port) {
    this.port = port;
    this.serverSocketId = nextSeqId();
    native_createServerSocket(this.port);
  }

  /**
   * Generates unique ServerSocket IDs
   * 
   * @return the next ID
   */
  private int nextSeqId() {
    int currSeqId = ServerSocket.seq_serverSocketId;
    seq_serverSocketId++;
    return currSeqId;
  }

  /**
   * 
   * @return
   * @throws IOException
   */
  public Socket accept() throws SocketException {
    Socket s = new Socket();

    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }

    // native_accept(this.port);
    return s;
  }

  // TODO clean up?
  /**
   * 
   */
  @Override
  public void close() throws IOException {
    this.isClosed = true;
    // native_closeServerSocket(this.serverSocketId);
  }

  public boolean isClosed() {
    return this.isClosed;
  }

  private native void native_createServerSocket(int port);

  private native int native_accept(int port);

  // private native void native_closeServerSocket(int port);

  // private native boolean native_isServerSocketClosed(int serverSocketId);
}
