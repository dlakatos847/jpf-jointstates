package java.io;


import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public class OutputStream implements Closeable, Flushable {
  private int socketId;

  public OutputStream(int socketId) {
    this.socketId = socketId;
  }

  @Override
  public void flush() throws IOException {
    native_flush(socketId);
  }

  @Override
  public void close() throws IOException {
    native_close(socketId);
  }

  public void write(int b) throws IOException {
    write(new byte[] { (byte) b }, 0, 1);
  }

  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    native_write(socketId, b, off, len);
  }

  private native void native_flush(int socketId) throws IOException;

  private native void native_close(int socketId) throws IOException;

  private native int native_write(int socketId, byte[] b, int off, int len);

}
