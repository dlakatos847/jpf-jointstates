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
import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class CommandDelegator implements Runnable {
  private static CommandDelegator cd;
  public static Object lastFlag = null;
  public static int lastJointStateDepth = 0;

  private Thread cmdThread;
  protected BlockingQueue<Command> receivedCommands = new LinkedBlockingQueue<Command>();
  protected BlockingQueue<ProvidedData> providedReplies = new LinkedBlockingQueue<ProvidedData>();
  protected int listenPort;

  /**
   * 
   */
  public CommandDelegator(Config config) {
    super();
//    @formatter:off
//    if (JointstatesSearch.side == Side.CLIENT) {
//      this.listenPort = Integer.parseInt(config.getString("jointstates.command.clientport"));
//    } else if (JointstatesSearch.side == Side.SERVER) {
//      this.listenPort = Integer.parseInt(config.getString("jointstates.command.serverport"));
//    }
//    @formatter:on
    this.cmdThread = new Thread(this);
    // this.cmdThread.start();
  }

  public static void initialize(Config config) {
    cd = new CommandDelegator(config);
  }

  public static void stop() {
    cd.cmdThread.interrupt();
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
    ObjectOutputStream oos;
    try {
      ss = new ServerSocket(this.listenPort);
      while (!Thread.interrupted()) {
        s = ss.accept();
        ois = new ObjectInputStream(s.getInputStream());
        this.receivedCommands.add((Command) ois.readObject());
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.writeObject(this.providedReplies.take());
        oos.close();
        ois.close();
        s.close();
      }
      ss.close();
    } catch (ClassNotFoundException | IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void signalReady() {

  }

  public static Command nextCommand() throws InterruptedException {
    return new Command(Side.CLIENT, CommandType.EXPLORE, 0, null);
    // return cd.receivedCommands.take();
  }

  public static void provideReply(ProvidedData data) {
    cd.providedReplies.add(data);
  }
}