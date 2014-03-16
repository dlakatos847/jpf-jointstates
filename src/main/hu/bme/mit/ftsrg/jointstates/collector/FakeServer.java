/* Copyright (C) 2007 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 *
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 *
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 */
package hu.bme.mit.ftsrg.jointstates.collector;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class FakeServer implements Runnable {
  Thread thread;
  int port = -1;
  BlockingQueue<Integer> message = new LinkedBlockingQueue<Integer>();

  public FakeServer(int port) {
    this.port = port;
    this.thread = new Thread(this);
    if (port != 0) {
      this.thread.start();
    }
  }

  @Override
  public void run() {
    try {
      ServerSocket serverSocket = new ServerSocket(this.port);
      Socket socket = serverSocket.accept();
      InputStream is = socket.getInputStream();
      Integer input = is.read();
      this.message.put(input);
      socket.close();
      serverSocket.close();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public Integer getMessage() {
    // TODO Integer returnValue = null;
    // try {
    // returnValue = this.message.take();
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    return 0;
  }
}
