package java.net;



import java.io.Closeable;
import java.io.IOException;

public class ServerSocket implements Closeable {

  private int serverSocketId = -1;
  private static int seq_serverSocketId = 0;
  private int port = -1;
  
  public ServerSocket(int port){
    this.port = port;
    serverSocketId = seq_serverSocketId;
    seq_serverSocketId++;
    native_createServerSocket(this.serverSocketId, this.port);
  }
  
  @Override
  public void close() throws IOException {
    native_closeServerSocket(this.serverSocketId);
  }
  
  public Socket accept(){
    Socket s = new Socket();
    native_accept(s.getSocketId());
    return s;
  }
  
  private native void native_createServerSocket(int serverSocketId, int port);

  private native void native_closeServerSocket(int serverSocketId);
  
  private native int native_accept(int socketId);
}
