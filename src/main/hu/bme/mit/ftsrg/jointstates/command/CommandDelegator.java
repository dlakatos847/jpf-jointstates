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

import gov.nasa.jpf.Config;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class CommandDelegator implements Runnable {
  protected BlockingQueue<ProvidedData> providedData = new LinkedBlockingQueue<ProvidedData>();
  protected BlockingQueue<Command> receivedCommands = new LinkedBlockingQueue<Command>();
  protected int listenPort = -1;
  protected Thread commandThread;

  /**
   * 
   */
  public CommandDelegator(Config config) {
    super();
    this.listenPort = Integer.parseInt(config.getString("jointstates.side"));
    this.commandThread = new Thread(this);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    ServerSocket ss = new ServerSocket(this.listenPort);
    Socket s;
    while (!Thread.interrupted()) {
      try {
        s = ss.accept();

      } catch (SocketException se) {

      }
    }
  }

  public void start() {
    this.commandThread.start();
  }

  public void stop() {

  }

}
