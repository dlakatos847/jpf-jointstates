package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class JPF_java_net_ServerSocket extends NativePeer {
  
  private ServerSocket serverSocket;
  public static Map<Integer,ServerSocket> serverSocketMapping;
  
  static{
    serverSocketMapping = new Hashtable<Integer,ServerSocket>();
  }

  @MJI
  public void native_closeServerSocket__I__V(MJIEnv env, int objRef, int v0) {
  }

  @MJI
  public void native_createServerSocket__II__V (MJIEnv env, int objRef, int v0, int v1) throws IOException {
    serverSocket = new ServerSocket(v1);
    serverSocketMapping.put(v0, serverSocket);
  }

  @MJI
  public int native_accept__I__I(MJIEnv env, int objRef, int v0) {
    int v = (int) 0;
    Socket s = serverSocket.accept();
    JPF_java_net_Socket.socketMapping.put(v0, s);
    return v;
  }

}
