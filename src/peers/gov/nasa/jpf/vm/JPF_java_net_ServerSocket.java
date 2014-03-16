package gov.nasa.jpf.vm;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.annotation.MJI;
import hu.bme.mit.ftsrg.jointstates.collector.PortCollector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class JPF_java_net_ServerSocket extends NativePeer {
  private static Logger log = JPF.getLogger(JPF_java_net_ServerSocket.class.getCanonicalName());
  private static Map<Integer, ServerSocket> serverSocketPorts;

  static {
    serverSocketPorts = new Hashtable<Integer, ServerSocket>();
  }

  @MJI
  public int native_accept__II__I(MJIEnv env, int objRef, int v0, int v1) throws IOException {
    ServerSocket ss = serverSocketPorts.get(v0);
    Socket s;

    log.info("SERVERSOCKET.ACCEPT " + v0);
    PortCollector.addPort(v0);

    s = ss.accept();

    // TODO @David Lakatos: i don't like this
    JPF_java_net_Socket.socketMapping.put(v1, s);
    return 0;
  }

  @MJI
  public void native_createServerSocket__I__V(MJIEnv env, int objRef, int v0) throws IOException {
    if (!serverSocketPorts.containsKey(v0)) {
      serverSocketPorts.put(v0, new ServerSocket(v0));
    }
  }

}
