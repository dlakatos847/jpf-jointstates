package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerSocket implements Closeable {

  private static int acceptDepth = 0;

  /**
   * Sequence for unique ID generation
   */
  private static int seq_serverSocketId;

  /**
   * serverSocketId -> ServerSocket instance mapping
   */
  private static Map<Integer, ServerSocket> serverSocketMapping;

  private int serverSocketId = -1;
  private int port = -1;
  private boolean isClosed = false;

  static {
    seq_serverSocketId = 0;
    serverSocketMapping = new HashMap<Integer, ServerSocket>();
  }

  public ServerSocket(int port) {
    this.port = port;
    this.serverSocketId = nextSeqId();
    ServerSocket.serverSocketMapping.put(this.serverSocketId, this);
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
    Socket s;

    /*
     * Stepping ServerSocket.accept() depth
     */
    ServerSocket.acceptDepth++;

    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }

    s = new Socket();
    System.out.println("Calling native_accept");
    native_accept(this.port, s.getSocketId());
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
    // return native_isServerSocketClosed(serverSocketId);
  }

  private native void native_createServerSocket(int port);

  private native int native_accept(int port, int socketId);

  // private native void native_closeServerSocket(int port);

  // private native boolean native_isServerSocketClosed(int serverSocketId);
}
