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
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class Commander implements Runnable {
  private static Commander cmd;

  private Thread cmdThread;
  protected Map<Side, Integer> sidePorts = new HashMap<Side, Integer>();
  public BlockingQueue<Command> commands = new LinkedBlockingQueue<Command>();
  public BlockingQueue<ProvidedData> replies = new LinkedBlockingQueue<ProvidedData>();

  public Commander(Config config) {
    this.sidePorts.put(Side.CLIENT, config.getInt("jointstates.command.clientport", 62301));
    this.sidePorts.put(Side.SERVER, config.getInt("jointstates.command.serverport", 62302));
    this.cmdThread = new Thread(this);
    this.cmdThread.start();
  }

  public static void initialize(Config config) {
    cmd = new Commander(config);
  }

  public static void terminate() {
    cmd.cmdThread.interrupt();
  }

  public static ProvidedData issueCommand(Command command) throws InterruptedException {
    cmd.commands.add(command);
    return cmd.replies.take();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try {
      while (!Thread.interrupted()) {
        Command command = this.commands.take();
        ProvidedData response;
        Socket s = new Socket(InetAddress.getByName("localhost"), this.sidePorts.get(command.getReceiver()));
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        oos.writeObject(command);
        response = (ProvidedData) ois.readObject();
        this.replies.add(response);
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InterruptedException f) {
      // it's normal
    }
  }
}
