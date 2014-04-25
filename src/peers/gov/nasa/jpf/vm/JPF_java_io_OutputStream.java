package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class JPF_java_io_OutputStream extends NativePeer {
  @MJI
  public void native_close__I__V(MJIEnv env, int objRef, int v0) throws IOException {
    // JPF_java_net_Socket.socketMapping.get(v0).close();
  }

  @MJI
  public void native_flush__I__V(MJIEnv env, int objRef, int v0) {
    // TODO
  }

  @MJI
  public int native_write___3BIII__I(MJIEnv env, int objRef, int byteArrayRef, int offset, int length, int lastJointStateId) throws IOException {
    byte[] byteArray = env.getByteArrayObject(byteArrayRef);
    Socket s = new Socket(Inet4Address.getByName("localhost"), 8081);
    OutputStream os = s.getOutputStream();
    InputStream is = s.getInputStream();
    int newJointStateId;

    os.write(lastJointStateId);
    os.write(byteArray);
    newJointStateId = is.read();
    s.close();

    return newJointStateId;
  }

  @MJI
  public void native_writeDepthIncremented__I__V(MJIEnv env, int objRef, int writeDepth) {
    logger.warning("jointstates write depth incremented to " + writeDepth);
  }
}
