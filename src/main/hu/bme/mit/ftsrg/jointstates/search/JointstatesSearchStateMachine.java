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
package hu.bme.mit.ftsrg.jointstates.search;

import gov.nasa.jpf.JPF;
import hu.bme.mit.ftsrg.jointstates.command.CommandDelegator;
import hu.bme.mit.ftsrg.jointstates.command.Message;
import hu.bme.mit.ftsrg.jointstates.command.MessageType;
import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesSearchStateMachine {
  protected static final Logger logger = JPF.getLogger(JointstatesSearchStateMachine.class.getCanonicalName());
  static JointstatesSearchState searchState = JointstatesSearchState.NORMAL;

  public static boolean advanceSearchState(JointstatesSearchState newSearchState, boolean newLevel, int jointStatesDepth) {
    if (!newLevel) {
      return true;
    }

    try {
      if (searchState == JointstatesSearchState.NORMAL && newSearchState == JointstatesSearchState.WRITE) {
        normalToWrite(jointStatesDepth);
      } else if (searchState == JointstatesSearchState.NORMAL && newSearchState == JointstatesSearchState.READ) {
        normalToWrite(jointStatesDepth);
        writeToRead(jointStatesDepth);
      } else if (searchState == JointstatesSearchState.WRITE && newSearchState == JointstatesSearchState.READ) {
        writeToRead(jointStatesDepth);
      }
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
      return false;
    }

    return true;
  }

  private static void normalToWrite(int jointStatesDepth) throws InterruptedException {
    CommandDelegator.sendMessage(new Message(jointStatesDepth, CommandDelegator.getSide(), Side.COMMANDER, MessageType.WRITEREADY));
    assert CommandDelegator.receiveMessage().getMsgType() == MessageType.WRITE;
  }

  private static void writeToRead(int jointStatesDepth) throws InterruptedException {
    CommandDelegator.sendMessage(new Message(jointStatesDepth, CommandDelegator.getSide(), Side.COMMANDER, MessageType.READREADY));
    assert CommandDelegator.receiveMessage().getMsgType() == MessageType.READ;
  }
}
