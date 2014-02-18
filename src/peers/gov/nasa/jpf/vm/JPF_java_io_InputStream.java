package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

public class JPF_java_io_InputStream extends NativePeer {

  @MJI
  public int native_read__I__I (MJIEnv env, int objRef, int v0) {
    return JPF_java_net_Socket.socketMapping.get(v0).getInputStream().read();
  }
}
