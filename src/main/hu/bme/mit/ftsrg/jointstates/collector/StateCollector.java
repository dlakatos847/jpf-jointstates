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
package hu.bme.mit.ftsrg.jointstates.collector;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class StateCollector {
  private static final Queue<BfsState> bfsRestorableStates = new LinkedList<BfsState>();
  private static final Queue<ApproachedState> approachedStates = new LinkedList<ApproachedState>();

  public static void addBfsState(BfsState state) {
    bfsRestorableStates.add(state);
  }

  public static BfsState getBfsState() {
    return bfsRestorableStates.poll();
  }

  public static int getBfsStateCount() {
    return bfsRestorableStates.size();
  }

  public static void addApproachedState(ApproachedState state) {
    approachedStates.add(state);
  }

  public static ApproachedState getApproachedState() {
    return approachedStates.poll();
  }

  public static int getApproachedStateCount() {
    return approachedStates.size();
  }
}
