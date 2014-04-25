package java.io;

import hu.bme.mit.ftsrg.jointstates.JointStateMatcher;

public class OutputStream implements Closeable, Flushable {
  private int socketId;
  private static int writeDepth = 0;

  public OutputStream(int socketId) {
    this.socketId = socketId;
  }

  @Override
  public void flush() throws IOException {
    native_flush(this.socketId);
  }

  @Override
  public void close() throws IOException {
    native_close(this.socketId);
  }

  public void write(int b) throws IOException {
    write(new byte[] { (byte) b }, 0, 1);
  }

  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    JointStateMatcher.jointStateId = native_write(b, off, len, JointStateMatcher.jointStateId);
    writeDepth++;
    native_writeDepthIncremented(writeDepth);
  }

  private native void native_flush(int socketId) throws IOException;

  private native void native_close(int socketId) throws IOException;

  private native int native_write(byte[] b, int off, int len, int lastJointStateId) throws IOException;

  private native void native_writeDepthIncremented(int writeDepth);
}
