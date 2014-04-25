package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;

public class JPF_java_net_Socket extends NativePeer {

  @MJI
  public void native_createSocket__I__V(MJIEnv env, int objRef, int v0) {

  }

  @MJI
  public void native_createSocket__ILjava_lang_String_2I__V(MJIEnv env, int objRef, int v0, int hostnameRef, int port) throws IOException {
    String hostname = env.getStringObject(hostnameRef);
    logger.warning("jointstates Socket.create() " + hostname + ":" + port);
  }

  @MJI
  public void native_closeSocket____V(MJIEnv env, int objRef) {
    logger.warning("jointstates Socket.close()");
  }
}
