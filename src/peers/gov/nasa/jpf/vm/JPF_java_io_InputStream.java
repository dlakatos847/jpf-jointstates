package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class JPF_java_io_InputStream extends NativePeer {

  @MJI
  public int native_read__I___3I(MJIEnv env, int objRef, int v0) throws IOException, ClassNotFoundException {
    Socket s = new Socket(Inet4Address.getByName("localhost"), 8082);
    int readDepth = env.getStaticIntField("java.io.InputStream", "readDepth");
    s.getOutputStream().write(readDepth);
    int[] messages = (int[]) (new ObjectInputStream(s.getInputStream())).readObject();
    s.close();
    return env.newIntArray(messages);
    // return JPF_java_net_Socket.socketMapping.get(v0).getInputStream().read();
  }
}
