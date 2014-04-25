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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.search.heuristic.SimplePriorityHeuristic;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import hu.bme.mit.ftsrg.jointstates.command.CommandDelegator;
import hu.bme.mit.ftsrg.jointstates.command.MessageType;
import hu.bme.mit.ftsrg.jointstates.core.JointstatesInstructionFactory;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesHeuristicSearch extends SimplePriorityHeuristic {
  protected static final Logger logger = JPF.getLogger(JointstatesHeuristicSearch.class.getCanonicalName());
  private static int PRIORITY_OFFSET = 100;
  boolean isInitialized = false;
  int lastJointStatesDepth = -1;

  /**
   * @param config
   * @param vm
   */
  public JointstatesHeuristicSearch(Config config, VM vm) {
    super(config, vm);
    // TODO Auto-generated constructor stub
  }

  // Initialize Joint States
  private boolean initialize() {
    try {
      if (CommandDelegator.receiveMessage().getMsgType() != MessageType.INIT) {
        logger.severe("jointstates initialization failed");
        terminate();
        return false;
      } else {
        this.isInitialized = true;
      }
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
      CommandDelegator.terminate();
      terminate();
      return false;
    }
    return true;
  }

  /*
   * The heuristic goes this way. Do a DFS search but don't cross joint state
   * levels. When out of uninteresting states produced by DFS, get the next
   * interesting joint state and explore its state space by DFS.
   * @see
   * gov.nasa.jpf.search.heuristic.SimplePriorityHeuristic#computeHeuristicValue
   * ()
   */
  @Override
  protected int computeHeuristicValue() {
    int currentJointStatesDepth = getJointStatesDepth();
    int nextJointStatesDepth = currentJointStatesDepth + 1;

    ThreadInfo ti = this.vm.getCurrentThread();
    Object attr = null;

    if (ti != null) {
      if (ti.getPC() != null) {
        attr = ti.getPC().getAttr();
      }
    }

    if (attr != null) {
      if (attr == JointstatesInstructionFactory.readFlag) {
        // has lower priority than the write tasks
        return Integer.MAX_VALUE - PRIORITY_OFFSET + 2 * nextJointStatesDepth;
      } else if (attr == JointstatesInstructionFactory.writeFlag) {
        // has higher priority than the read tasks
        return Integer.MAX_VALUE - PRIORITY_OFFSET + 2 * nextJointStatesDepth - 1;
      }
    }

    // -100 is because we would like to avoid priority collisions between the
    // original DFS states and the joint states
    return Integer.MAX_VALUE - PRIORITY_OFFSET - this.vm.getPathLength() - 1;
  }

  /*
   * (non-Javadoc)
   * @see gov.nasa.jpf.search.heuristic.HeuristicSearch#generateChildren()
   */
  @Override
  protected boolean generateChildren() {
    if (!this.isInitialized) {
      initialize();
    }

    ThreadInfo ti = this.vm.getCurrentThread();
    Object attr = null;

    if (ti != null) {
      if (ti.getPC() != null) {
        attr = ti.getPC().getAttr();
      }
    }
    if (attr != null) {
      // If write state loaded
      if (attr == JointstatesInstructionFactory.writeFlag) {
        logger.warning("jointstates is in write state where current jointstate depth is " + getJointStatesDepth());
        if (!JointstatesSearchStateMachine.advanceSearchState(JointstatesSearchState.WRITE, getJointStatesDepth() > this.lastJointStatesDepth,
            getJointStatesDepth())) {
          logger.severe("jointstates error occurred");
          terminate();
          return false;
        }
        this.lastJointStatesDepth = getJointStatesDepth();
      }
      // If read state loaded
      else if (attr == JointstatesInstructionFactory.readFlag) {
        logger.warning("jointstates is in read state where current jointstate depth is " + getJointStatesDepth());
        if (!JointstatesSearchStateMachine.advanceSearchState(JointstatesSearchState.READ, getJointStatesDepth() > this.lastJointStatesDepth,
            getJointStatesDepth())) {
          logger.severe("jointstates error occurred");
          terminate();
          return false;
        }
        this.lastJointStatesDepth = getJointStatesDepth();
      }
    }

    return super.generateChildren();
  }

  /**
   * 
   * @return The current Jointstates depth
   */
  private int getJointStatesDepth() {
    int readDepth = 0;
    int writeDepth = 0;

    // Default class loader
    ClassLoaderInfo cli = this.vm.getSystemState().getKernelState().classLoaders.get(0);

    // update the write depth if the class OutputStream has been loaded
    if (cli.getResolvedClassInfo("java.io.OutputStream") != null) {
      writeDepth = (int) this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.OutputStream")
          .getStaticFieldValueObject("writeDepth");
    }
    // update the read depth if the class InputStream has been loaded
    else if (cli.getResolvedClassInfo("java.io.InputStream") != null) {
      readDepth = (int) this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.InputStream")
          .getStaticFieldValueObject("readDepth");
    }

    return readDepth + writeDepth;
  }
}
