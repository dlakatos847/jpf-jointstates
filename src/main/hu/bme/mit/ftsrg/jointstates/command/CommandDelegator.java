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
import gov.nasa.jpf.JPF;
import hu.bme.mit.ftsrg.jointstates.core.Side;
import hu.bme.mit.ftsrg.jointstates.listener.JointstatesListener;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class CommandDelegator {
  protected static final Logger logger = JPF.getLogger(CommandDelegator.class.getCanonicalName());
  private static CommandDelegator cd;
  public static Object lastFlag = null;
  public static int lastJointStateDepth = 0;

  Side side = null;
  Thread sendThread;
  Thread receiveThread;
  MessageSender sendInstance;
  MessageReceiver receiveInstance;

  /**
   * 
   */
  public CommandDelegator(Config config) {
    super();

    String sideConfig = config.getString("jointstates.side");

    if (sideConfig != null) {
      if (sideConfig.equals("client")) {
        this.side = Side.CLIENT;
      } else if (sideConfig.equals("server")) {
        this.side = Side.SERVER;
      } else {
        logger.severe("jointstates.side parameter has invalid value. Allowed values are: [client, server]");
        return;
      }
    } else {
      logger.severe("jointstates.side parameter is missing. Allowed values are: [client, server]");
      return;
    }

    this.sendInstance = new MessageSender(logger);
    this.receiveInstance = new MessageReceiver(this.side, logger);
    this.sendThread = new Thread(this.sendInstance, JointstatesListener.side + "_SENDER");
    this.sendThread.setDaemon(true);
    this.receiveThread = new Thread(this.receiveInstance, JointstatesListener.side + "_RECEIVER");
    this.receiveThread.setDaemon(true);
    this.sendThread.start();
    this.receiveThread.start();
  }

  public static void initialize(Config config) {
    cd = new CommandDelegator(config);
    try {
      sendMessage(new Message(0, cd.side, Side.COMMANDER, MessageType.INIT));
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
      terminate();
    }
  }

  public static Side getSide() {
    return cd.side;
  }

  public static void end() {
    // cd.sendThread.interrupt();
    // cd.receiveThread.interrupt();
    logger.warning("jointstates command delegator stopped successfully");
  }

  public static void terminate() {
    logger.warning("jointstates command delegator termination");
    try {
      sendMessage(new Message(0, cd.side, Side.COMMANDER, MessageType.ERROR));
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
    }
    end();
  }

  public static Message receiveMessage() throws InterruptedException {
    return cd.receiveInstance.inboundQueue.take();
  }

  public static void sendMessage(Message msg) throws InterruptedException {
    cd.sendInstance.sendMessage(msg);
  }
}