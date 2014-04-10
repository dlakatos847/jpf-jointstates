package java.io;

import gov.nasa.jpf.vm.Verify;
import hu.bme.mit.ftsrg.jointstates.JointStateMatcher;

public class InputStream implements Closeable {
  private int socketId = -1;
  private static int readDepth = 0;

  public InputStream(int socketId) {
    this.socketId = socketId;
  }

  public int read() {
    int[] transitions = native_read(JointStateMatcher.lastJointStateId);

    // this call branches the state space according to the possible JointState
    // transitions
    int index = Verify.getInt(0, (transitions.length / 2) - 1);

    // set the next JointStateId
    JointStateMatcher.lastJointStateId = transitions[index * 2];

    // return the message
    return transitions[index * 2 + 1];
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

  private native int[] native_read(int lastJointStateId);

}
