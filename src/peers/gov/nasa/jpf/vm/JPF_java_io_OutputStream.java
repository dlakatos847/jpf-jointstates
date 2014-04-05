package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;

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
  public int native_write__I_3BII__I(MJIEnv env, int objRef, int socketIndex, int byteArrayRef, int offset, int length) throws IOException {
    byte[] byteArray = env.getByteArrayObject(byteArrayRef);
    int writeDepth = env.getStaticIntField("java.io.OutputStream", "writeDepth");

    // JPF_java_net_Socket.socketMapping.get(socketIndex).getOutputStream().write(byteArray,
    // offset, length);

    Socket s = new Socket(Inet4Address.getByName("localhost"), 8081);
    OutputStream os = s.getOutputStream();
    os.write(writeDepth);
    os.write(byteArray);
    s.close();
    return 0;
  }
}
