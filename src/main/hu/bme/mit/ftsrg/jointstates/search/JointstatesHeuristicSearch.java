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
    logger.warning("jointstates initialization started");
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
    logger.warning("jointstates initialization succeeded");
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
    int heuristicValue = -1;
    int currentJointStatesDepth = getJointStatesDepth();
    int nextJointStatesDepth = currentJointStatesDepth + 1;

    ThreadInfo ti = this.vm.getCurrentThread();
    Object attr = null;

    if (ti != null) {
      if (ti.getPC() != null) {
        attr = ti.getPC().getAttr();
      }
    }

    // The state is a joint state
    if (attr != null) {
      if (attr == JointstatesInstructionFactory.writeFlag) {
        logger.warning("jointstates computing heuristic value for write state");

        // has higher priority than the read tasks (-1)
        heuristicValue = Integer.MAX_VALUE - PRIORITY_OFFSET + 2 * nextJointStatesDepth - 1;
      } else if (attr == JointstatesInstructionFactory.readFlag) {
        logger.warning("jointstates computing heuristic value for read state");

        // has lower priority than the write tasks (missing -1)
        heuristicValue = Integer.MAX_VALUE - PRIORITY_OFFSET + 2 * nextJointStatesDepth;
      }
    }
    // The state is a normal state (simple DFS)
    else {
      logger.warning("jointstates computing heuristic value for normal state");

      heuristicValue = Integer.MAX_VALUE - PRIORITY_OFFSET - this.vm.getPathLength() - 1;
    }

    logger.warning("jointstates computed heuristic value " + heuristicValue);

    // -100 is because we would like to avoid priority collisions between the
    // original DFS states and the joint states
    return heuristicValue;
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
        logger.warning("jointstates loaded write state [" + this.parentState.getStateId() + "] where current jointstate depth is " + getJointStatesDepth());
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
        logger.warning("jointstates loaded read state [" + this.parentState.getStateId() + "] where current jointstate depth is " + getJointStatesDepth());
        if (!JointstatesSearchStateMachine.advanceSearchState(JointstatesSearchState.READ, getJointStatesDepth() > this.lastJointStatesDepth,
            getJointStatesDepth())) {
          logger.severe("jointstates error occurred");
          terminate();
          return false;
        }
        this.lastJointStatesDepth = getJointStatesDepth();
      }
    } else {
      logger.warning("jointstates loaded normal state [" + this.parentState.getStateId() + "] where current jointstate depth is " + getJointStatesDepth());
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

    // Default class loader used to reach the SuT classes' state (static
    // variables)
    ClassLoaderInfo cli = this.vm.getSystemState().getKernelState().classLoaders.get(0);

    // update the write depth if the class OutputStream has been loaded
    if (cli.getResolvedClassInfo("java.io.OutputStream") != null) {
      writeDepth = (int) this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.OutputStream")
          .getStaticFieldValueObject("writeDepth");
    } else {
      logger.severe("jointstates java.io.OutputStream class not loaded");
      terminate();
    }

    // update the read depth if the class InputStream has been loaded
    if (cli.getResolvedClassInfo("java.io.InputStream") != null) {
      readDepth = (int) this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.InputStream")
          .getStaticFieldValueObject("readDepth");
    } else {
      logger.severe("jointstates java.io.InputStream class not loaded");
      terminate();
    }

    return readDepth + writeDepth;
  }
}
