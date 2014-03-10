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

import gov.nasa.jpf.vm.RestorableVMState;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class StateCollector {
  private static StateCollector sc = new StateCollector();

  private final Queue<RestorableVMState> restorableClientStates = new LinkedList<RestorableVMState>();
  private final Queue<RestorableVMState> restorableServerStates = new LinkedList<RestorableVMState>();

  public static void addClientState(RestorableVMState state) {
    sc.restorableClientStates.add(state);
  }

  public static void addServerState(RestorableVMState state) {
    sc.restorableServerStates.add(state);
  }

  public static RestorableVMState getClientState() {
    return sc.restorableClientStates.poll();
  }

  public static RestorableVMState getServerState() {
    return sc.restorableServerStates.poll();
  }

  public static Integer getClientStateSize() {
    return sc.restorableClientStates.size();
  }

  public static Integer getServerStateSize() {
    return sc.restorableServerStates.size();
  }
}
