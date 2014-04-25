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
    int lastJointStateId = JointStateMatcher.jointStateId;
    int[] transitions = native_read(JointStateMatcher.jointStateId, readDepth);

    // increment read depth and log it
    readDepth++;
    native_readDepthIncremented(readDepth);

    // this call branches the state space according to the possible Joint State
    // transitions
    int index = Verify.getInt(0, (transitions.length / 2) - 1);

    // set the next Joint State ID and log it
    JointStateMatcher.jointStateId = transitions[index * 2];
    native_jointStateIdSet(lastJointStateId, JointStateMatcher.jointStateId);

    // return the chosen message
    return transitions[index * 2 + 1];
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

  private native int[] native_read(int lastJointStateId, int readDepth);

  private native void native_readDepthIncremented(int readDepth);

  private native void native_jointStateIdSet(int lastJointStateId, int newJointStateId);

}
