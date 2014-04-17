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

import hu.bme.mit.ftsrg.jointstates.command.Aggregator.AggregatorType;
import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class Commander implements Runnable {
  protected static final Logger logger = Logger.getLogger(Commander.class.getCanonicalName());
  private static Commander commander;
  static boolean done = false;
  static Aggregator addMessageAggregator = new Aggregator(AggregatorType.ADD);
  static Aggregator queryMessageAggregator = new Aggregator(AggregatorType.QUERY);

  Thread sendThread;
  Thread receiveThread;
  MessageSender sendInstance;
  MessageReceiver receiveInstance;

  public Commander() {
    this.sendInstance = new MessageSender();
    this.receiveInstance = new MessageReceiver(Side.COMMANDER);
    this.sendThread = new Thread(this.sendInstance);
    this.receiveThread = new Thread(this.receiveInstance);
    this.sendThread.start();
    this.receiveThread.start();
  }

  public static void initialize() {
    commander = new Commander();
    logger.info("jointstates commander is ready to initialize command subsystem");

    addMessageAggregator.start();
    queryMessageAggregator.start();

    try {
      Message msg;

      // echo requests
      for (int i = 0; i < 2; ++i) {
        msg = receiveMessage();
        if (msg != Message.INIT) {
          logger.severe("jointstates commander initialization failed");
          terminate();
        } else {
          logger.info("jointstates commander received init message");
        }
      }

      // echo replies
      sendMessageToClient(Message.INIT);
      sendMessageToServer(Message.INIT);

    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
      terminate();
    }
  }

  public static void terminate() {
    logger.warning("jointstates commander termination");
    done = true;
    try {
      sendMessageToClient(Message.ERROR);
      sendMessageToServer(Message.ERROR);
    } catch (InterruptedException e) {
      logger.severe("jointstates interrupt occurred during termination");
    }
    end();
  }

  public static void end() {
    done = true;
    commander.sendThread.interrupt();
    commander.receiveThread.interrupt();
    logger.info("jointstates command delegator stopped successfully");
  }

  public static void sendMessageToClient(Message msg) throws InterruptedException {
    commander.sendInstance.sendMessage(Side.CLIENT, msg);
  }

  public static void sendMessageToServer(Message msg) throws InterruptedException {
    commander.sendInstance.sendMessage(Side.SERVER, msg);
  }

  public static Message receiveMessage() throws InterruptedException {
    return commander.receiveInstance.receiveMessage();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {

  }

  public static void main(String[] args) {
    Commander.initialize();

    while (!done) {
      if (true) {
        done = true;
      }
    }

    Commander.end();
  }
}
