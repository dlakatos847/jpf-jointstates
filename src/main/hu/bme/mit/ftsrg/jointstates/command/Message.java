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

import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.io.Serializable;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class Message implements Serializable {
  private static final long serialVersionUID = 6408914686817755165L;
  int jointStatesDepth;
  Side source;
  Side destination;
  MessageType msgType;

  /**
   * @param jointStatesDepth
   * @param source
   * @param destination
   * @param msgType
   */
  public Message(int jointStatesDepth, Side source, Side destination, MessageType msgType) {
    super();
    this.jointStatesDepth = jointStatesDepth;
    this.source = source;
    this.destination = destination;
    this.msgType = msgType;
  }

  public void setDestination(Side destination) {
    this.destination = destination;
  }

  public int getJointStatesDepth() {
    return this.jointStatesDepth;
  }

  public Side getSource() {
    return this.source;
  }

  public Side getDestination() {
    return this.destination;
  }

  public MessageType getMsgType() {
    return this.msgType;
  }

}
