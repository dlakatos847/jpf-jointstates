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
public class Command implements Serializable {
  private static final long serialVersionUID = -3912483750386002090L;
  private Side receiver;
  private CommandType type;
  private int port;
  private Object message;

  /**
   * @param type
   * @param port
   * @param message
   */
  public Command(Side receiver, CommandType type, int port, Object message) {
    super();
    this.receiver = receiver;
    this.type = type;
    this.port = port;
    this.message = message;
  }

  public Side getReceiver() {
    return this.receiver;
  }

  public CommandType getType() {
    return this.type;
  }

  public int getPort() {
    return this.port;
  }

  public Object getMessage() {
    return this.message;
  }

}
