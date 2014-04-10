package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;
import hu.bme.mit.ftsrg.jointstates.command.JointStateTransition;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class JPF_java_io_InputStream extends NativePeer {

  @MJI
  public int native_read__I___3I(MJIEnv env, int objRef, int lastJointStateId) throws IOException, ClassNotFoundException {
    Socket s = new Socket(Inet4Address.getByName("localhost"), 8082);
    OutputStream os = s.getOutputStream();
    InputStream is = s.getInputStream();
    ObjectInputStream ois = new ObjectInputStream(is);
    JointStateTransition[] transitions;
    int[] retArray;
    int retArrayRef;

    // networking
    os.write(lastJointStateId);
    transitions = (JointStateTransition[]) ois.readObject();
    s.close();

    // prepare transitions for return
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
}
