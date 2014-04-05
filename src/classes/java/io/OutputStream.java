package java.io;

public class OutputStream implements Closeable, Flushable {
  private int socketId;
  private static int writeDepth;

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
    writeDepth++;
    System.out.println("writedepth advanced to " + writeDepth);
    native_write(this.socketId, b, off, len);
  }

  private native void native_flush(int socketId) throws IOException;

  private native void native_close(int socketId) throws IOException;

  private native int native_write(int socketId, byte[] b, int off, int len);

}
