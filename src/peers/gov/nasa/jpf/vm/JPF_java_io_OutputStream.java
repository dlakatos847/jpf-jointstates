package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;

public class JPF_java_io_OutputStream extends NativePeer {

  @MJI
  public void native_close__I__V(MJIEnv env, int objRef, int v0) throws IOException {
    JPF_java_net_Socket.socketMapping.get(v0).close();
  }

  @MJI
  public void native_flush__I__V(MJIEnv env, int objRef, int v0) {
    // TODO
  }

  @MJI
  public int native_write__I_3BII__I(MJIEnv env, int objRef, int v0, int r1, int v2, int v3) throws IOException {
    int v = (int) 0;
    byte[] b = env.getByteArrayObject(r1);

    JPF_java_net_Socket.socketMapping.get(v0).getOutputStream().write(b, v2, v3);

    return v;
  }

}
