package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;
import hu.bme.mit.ftsrg.jointstates.command.Aggregator;
import hu.bme.mit.ftsrg.jointstates.listener.JointstatesListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
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
  public int native_write___3BIII__I(MJIEnv env, int objRef, int messageRef, int offset, int length, int lastJointStateId) throws IOException {
    byte[] messageByteArray = env.getByteArrayObject(messageRef);
    Socket s = new Socket(Inet4Address.getByName("localhost"), Aggregator.AGGREGATOR_ADD_PORT);
    InputStream is = s.getInputStream();
    // ObjectInputStream ois = new ObjectInputStream(is);
    OutputStream os = s.getOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(os);
    int newJointStateId;

    os.write(lastJointStateId);
    oos.writeObject(JointstatesListener.otherSide);
    os.write(messageByteArray);
    newJointStateId = is.read();
    s.close();

    logger.warning("native write current JSID: " + lastJointStateId + ", recipient: " + JointstatesListener.otherSide + ", message: " + messageByteArray[0]
        + ", new JSID: " + newJointStateId);

    return newJointStateId;
  }

  @MJI
  public void native_writeDepthIncremented__I__V(MJIEnv env, int objRef, int writeDepth) {
    logger.warning("jointstates write depth incremented to " + writeDepth);
  }
}
