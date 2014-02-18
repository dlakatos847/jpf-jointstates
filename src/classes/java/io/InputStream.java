package java.io;

public class InputStream implements Closeable {

  private int socketId = -1;

  public InputStream(int socketId) {
    this.socketId = socketId;
  }

  public int read() {
    return native_read(socketId);
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

  private native int native_read(int socketId);

}
