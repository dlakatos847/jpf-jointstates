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
package hu.bme.mit.ftsrg.jointstates.command;

import gov.nasa.jpf.JPF;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class HeartbeatResponder extends Thread {
  private static Logger logger = JPF.getLogger(HeartbeatResponder.class.getName());

  private final int port;
  private final int expectedInput = 99;
  private final int output = 100;
  ServerSocket serverSocket;

  public HeartbeatResponder(int port) {
    setDaemon(false);
    this.port = port;
  }

  @Override
  public void run() {
    try {
      this.serverSocket = new ServerSocket(this.port);
      Socket s = this.serverSocket.accept();
      InputStream is = s.getInputStream();
      OutputStream os = s.getOutputStream();
      int input = is.read();
      if (input == this.expectedInput) {
        logger.info("jointstates heartbeat OK");
        os.write(this.output);
      } else {
        logger.severe("jointstates wrong heartbeat input received: " + input);
        os.write(-1);
      }
      is.close();
      os.close();
      s.close();
    } catch (SocketException e) {
      // it's normal
    } catch (IOException f) {
      logger.severe(f.getMessage());
    }
  }
}
