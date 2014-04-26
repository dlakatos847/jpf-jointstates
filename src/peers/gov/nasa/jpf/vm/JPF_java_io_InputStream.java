package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;
import hu.bme.mit.ftsrg.jointstates.command.Aggregator;
import hu.bme.mit.ftsrg.jointstates.command.JointStateTransition;
import hu.bme.mit.ftsrg.jointstates.listener.JointstatesListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class JPF_java_io_InputStream extends NativePeer {

  @MJI
  public int native_read__II___3I(MJIEnv env, int objRef, int lastJointStateId, int readDepth) throws IOException, ClassNotFoundException {
    logger.warning("jointstates read depth was " + readDepth + " before native reading");
    Socket s = new Socket(Inet4Address.getByName("localhost"), Aggregator.AGGREGATOR_QUERY_PORT);
    OutputStream os = s.getOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(os);
    InputStream is = s.getInputStream();
    ObjectInputStream ois = new ObjectInputStream(is);
    JointStateTransition[] transitions;
    int[] retArray;
    int retArrayRef;

    // networking
    logger.warning("jointstates querying possible transitions after joint state id " + lastJointStateId);
    os.write(lastJointStateId);
    oos.writeObject(JointstatesListener.side);
    transitions = (JointStateTransition[]) ois.readObject();
    s.close();

    // prepare transitions for return
    if (transitions.length == 0) {
      logger.severe("jointstates no transition defined after joint state id " + lastJointStateId + " on read depth " + readDepth);
    } else {
      String log = "jointstates read returned possible messages: ";
      for (int i = 0; i < transitions.length; ++i) {
        log += transitions[i].getMessage() + " ";
      }
      logger.warning(log);
    }
    retArrayRef = env.newIntArray(transitions.length * 2);
    retArray = env.getIntArrayObject(retArrayRef);
    int i = 0;
    for (JointStateTransition t : transitions) {
      retArray[i * 2] = t.getNextJointStateId();
      retArray[i * 2 + 1] = t.getMessage();
      i++;
    }

    return retArrayRef;
  }

  @MJI
  public void native_readDepthIncremented__I__V(MJIEnv env, int objRef, int readDepth) {
    logger.warning("jointstates read depth incremented from " + (readDepth - 1) + " to " + readDepth);
  }

  @MJI
  public void native_jointStateIdSet__II__V(MJIEnv env, int objRef, int lastJointStateId, int newJointStateId) {
    logger.warning("jointstates joint state id set from " + lastJointStateId + " to " + newJointStateId);
  }
}
