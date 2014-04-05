package java.io;

import gov.nasa.jpf.vm.Verify;

public class InputStream implements Closeable {
  private int socketId = -1;
  private static int readDepth = 0;

  public InputStream(int socketId) {
    this.socketId = socketId;
  }

  public int read() {
    readDepth++;
    int[] readMessages = native_read(this.socketId);
    for (int i : readMessages) {
      System.out.println("MODEL RECEIVED " + i);
    }
    return Verify.getIntFromList(readMessages);
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

  private native int[] native_read(int socketId);

}
