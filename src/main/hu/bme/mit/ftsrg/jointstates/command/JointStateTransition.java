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

import java.io.Serializable;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointStateTransition implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -4459367897428024204L;
  private int message;
  private int nextJointStateId;

  /**
   * @param message
   * @param nextJointStateId
   */
  public JointStateTransition(int message, int nextJointStateId) {
    super();
    this.message = message;
    this.nextJointStateId = nextJointStateId;
  }

  public int getMessage() {
    return this.message;
  }

  public void setMessage(int message) {
    this.message = message;
  }

  public int getNextJointStateId() {
    return this.nextJointStateId;
  }

  public void setNextJointStateId(int nextJointStateId) {
    this.nextJointStateId = nextJointStateId;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof JointStateTransition)) {
      return false;
    }

    JointStateTransition jst = (JointStateTransition) obj;
    return this.message == jst.getMessage();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return 31 * (41 + this.message);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[message: " + this.message + ", next jointstate id: " + this.nextJointStateId + "]";
  }
}
