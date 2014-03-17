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
import hu.bme.mit.ftsrg.jointstates.search.JointstatesSearch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class CommandDelegator implements Runnable {
  // private static CommandDelegator cd;

  protected BlockingQueue<ProvidedData> providedData = new LinkedBlockingQueue<ProvidedData>();
  protected BlockingQueue<Command> receivedCommands = new LinkedBlockingQueue<Command>();
  protected int listenPort = -1;
  protected Thread commandThread;

  /**
   * 
   */
  public CommandDelegator(Config config) {
    super();
    if (JointstatesSearch.side == JointstatesSearch.clientSide) {
      this.listenPort = Integer.parseInt(config.getString("jointstates.command.clientport"));
    } else if (JointstatesSearch.side == JointstatesSearch.serverSide) {
      this.listenPort = Integer.parseInt(config.getString("jointstates.command.serverport"));
    }
    this.commandThread = new Thread(this);
  }

  public static void initCommandDelegator(Config config) {
    // cd = new CommandDelegator(config);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    ServerSocket ss;
    Socket s;
    ObjectInputStream ois;
    try {
      ss = new ServerSocket(this.listenPort);
      while (!Thread.interrupted()) {
        s = ss.accept();
        ois = (ObjectInputStream) s.getInputStream();
        this.receivedCommands.add((Command) ois.readObject());
        ois.close();
        s.close();
      }
      ss.close();
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    this.commandThread.start();
  }

  public void stop() {

  }

  // TODO delete dummy return and uncomment the area below
  // TODO a nicer exception handling solution would be great
  public static Command nextCommand() {
    return new Command(CommandType.EXPLORE, 0, null);

    /*
     * Command nextCommand = null; while (nextCommand == null) { try {
     * nextCommand = cd.receivedCommands.take(); } catch (InterruptedException
     * e) { e.printStackTrace(); } } return nextCommand;
     */
  }
}
