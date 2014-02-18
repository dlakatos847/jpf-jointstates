package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class JPF_java_net_Socket extends NativePeer {
  public static Map<Integer, Socket> socketMapping;

  static {
    socketMapping = new Hashtable<Integer, Socket>();
  }

  @MJI
  public void native_createSocket__I__V(MJIEnv env, int objRef, int v0) {
    
  }

  @MJI
  public void native_createSocket__ILjava_lang_String_2I__V(MJIEnv env, int objRef, int v0, int rString1, int v2) throws IOException {
    String hostName = env.getStringObject(rString1);
    int port = v2;

    InetAddress addr = InetAddress.getByName(hostName);
    socketMapping.put(v0, new Socket(addr, port));

    System.out.println("SOCKET CREATED");
  }

  @MJI
  public void native_closeSocket__I__V(MJIEnv env, int objRef, int v0) throws IOException {
    socketMapping.get(v0).close();
    System.out.println("SOCKET CLOSED");
  }

}
