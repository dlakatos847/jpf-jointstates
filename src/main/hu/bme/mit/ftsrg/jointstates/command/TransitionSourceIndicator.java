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

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class TransitionSourceIndicator {
  private int parentJointStateId;
  private Side messageDestination;

  /**
   * @param parentJointStateId
   * @param destination
   */
  public TransitionSourceIndicator(int parentJointStateId, Side destination) {
    super();
    this.parentJointStateId = parentJointStateId;
    this.messageDestination = destination;
  }

  public int getParentJointStateId() {
    return this.parentJointStateId;
  }

  public Side getMessageDestination() {
    return this.messageDestination;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    TransitionSourceIndicator other = null;
    if (!(obj instanceof TransitionSourceIndicator)) {
      return false;
    } else {
      other = (TransitionSourceIndicator) obj;
    }
    return (getParentJointStateId() == other.getParentJointStateId() && getMessageDestination() == other.getMessageDestination());
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "parent joint states id: " + getParentJointStateId() + ", to " + getMessageDestination() + "]";
  }
}
