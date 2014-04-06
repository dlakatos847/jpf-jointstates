package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;
import java.util.logging.Level;

public class JPF_java_net_Socket extends NativePeer {

  @MJI
  public void native_createSocket__I__V(MJIEnv env, int objRef, int v0) {

  }

  @MJI
  public void native_createSocket__ILjava_lang_String_2I__V(MJIEnv env, int objRef, int v0, int hostnameRef, int port) throws IOException {
    String hostname = env.getStringObject(hostnameRef);
    logger.log(Level.FINEST, "SOCKET CREATE " + hostname + ":" + port);
  }

  @MJI
  public void native_closeSocket__V(MJIEnv env, int objRef) throws IOException {
    logger.log(Level.FINEST, "SOCKET CLOSE");
  }
}
