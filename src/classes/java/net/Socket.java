package java.net;

//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Socket {

  private int socketId = -1;
  private static int seq_socketId = 0;

  public Socket() {
    socketId = seq_socketId;
    seq_socketId++;
    native_createSocket(socketId);
  }

  public Socket(InetAddress addr, int port) {
    socketId = seq_socketId;
    seq_socketId++;
    native_createSocket(socketId, addr.getHostAddress(), port);
  }

  public InputStream getInputStream() {
    return new InputStream(socketId);
  }

  public OutputStream getOutputStream() throws IOException {
    return new OutputStream(socketId);
  }

  public void close() throws IOException {
    native_closeSocket(socketId);
  }

  @Override
  public String toString() {
    return "Model class socket";
  }

  private native void native_createSocket(int socketId);

  private native void native_createSocket(int socketId, String addr, int port);

  private native void native_closeSocket(int socketId);

  public int getSocketId() {
    return socketId;
  }

}