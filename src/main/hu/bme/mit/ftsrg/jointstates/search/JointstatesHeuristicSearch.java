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
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.choice.BreakGenerator;
import hu.bme.mit.ftsrg.jointstates.command.CommandDelegator;
import hu.bme.mit.ftsrg.jointstates.command.Message;
import hu.bme.mit.ftsrg.jointstates.core.JointstatesInstructionFactory;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesHeuristicSearch extends SimplePriorityHeuristic {
  protected static final Logger logger = JPF.getLogger(JointstatesHeuristicSearch.class.getCanonicalName());
  private static int PRIORITY_OFFSET = 100;
  JointstatesSearchState searchState = JointstatesSearchState.NONE;

  /**
   * @param config
   * @param vm
   */
  public JointstatesHeuristicSearch(Config config, VM vm) {
    super(config, vm);
    // TODO Auto-generated constructor stub
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
    int currentJointStatesDepth = getJointStateDepth();
    int nextJointStatesDepth = currentJointStatesDepth + 1;

    if (this.vm.getChoiceGenerator() instanceof BreakGenerator) {
      if (CommandDelegator.lastFlag == JointstatesInstructionFactory.readFlag) {
        // has lower priority than the write tasks
        return Integer.MAX_VALUE - PRIORITY_OFFSET + 2 * nextJointStatesDepth;
      } else if (CommandDelegator.lastFlag == JointstatesInstructionFactory.writeFlag) {
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
    if (this.searchState == JointstatesSearchState.NONE) {
      try {
        Message msg = CommandDelegator.receiveMessage();
        if (msg == Message.INIT) {
          CommandDelegator.sendMessage(Message.INIT);
          this.searchState = JointstatesSearchState.NORMAL;
        }
      } catch (InterruptedException e) {
        logger.severe(e.getMessage());
        CommandDelegator.terminate();
        terminate();
        return false;
      }
    }

    // If read state or write state loaded
    if (this.vm.getChoiceGenerator() instanceof BreakGenerator) {

      int currentJointStateDepth = getJointStateDepth() + 1;

      // If read state loaded
      if (this.vm.getChoiceGenerator().getId().equals("jointstates before read state")) {
        logger.info("jointstates is in read state\tjointstate depth: " + currentJointStateDepth);

      }
      // If write state loaded
      else if (this.vm.getChoiceGenerator().getId().equals("jointstates before write state")) {
        logger.info("jointstates is in write state\tjointstate depth: " + currentJointStateDepth);
      }
      // Error
      else {
        logger.severe("jointstates search is invalid state");
        terminate();
        return false;
      }
    }

    return super.generateChildren();
  }

  private int getJointStateDepth() {
    int readDepth = 0;
    int writeDepth = 0;

    // update the read depth if the class InputStream has been loaded
    if (this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.InputStream") != null) {
      readDepth = (int) this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.InputStream")
          .getStaticFieldValueObject("readDepth");
    }

    // update the write depth if the class OutputStream has been loaded
    if (this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.OutputStream") != null) {
      writeDepth = (int) this.vm.getSystemState().getKernelState().classLoaders.get(0).getResolvedClassInfo("java.io.OutputStream")
          .getStaticFieldValueObject("writeDepth");
    }

    return readDepth + writeDepth;
  }
}
