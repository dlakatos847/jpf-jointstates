package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;

public class JPF_java_net_ServerSocket extends NativePeer {
  @MJI
  public void native_createServerSocket__I__V(MJIEnv env, int objRef, int port) throws IOException {
    logger.info("SERVERSOCKET CREATE " + port);
  }

  @MJI
  public void native_accept__I__V(MJIEnv env, int objRef, int port) {
    logger.info("SERVERSOCKET ACCEPT " + port);
  }
}
