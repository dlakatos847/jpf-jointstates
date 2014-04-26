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
package hu.bme.mit.ftsrg.jointstates.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.InstanceInvocation;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.choice.BreakGenerator;
import hu.bme.mit.ftsrg.jointstates.command.CommandDelegator;
import hu.bme.mit.ftsrg.jointstates.core.JointstatesInstructionFactory;
import hu.bme.mit.ftsrg.jointstates.core.Side;
import hu.bme.mit.ftsrg.jointstates.search.JointstatesSearchStateMachine;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesListener extends ListenerAdapter {
  protected static final Logger logger = JPF.getLogger(JointstatesListener.class.getCanonicalName());
  public static Side side;
  public static Side otherSide;

  /*
   * Make sure jpf-jointstates master-JPF is available (non-Javadoc)
   * @see gov.nasa.jpf.ListenerAdapter#searchStarted(gov.nasa.jpf.search.Search)
   */
  @Override
  public void searchStarted(Search search) {
    super.searchStarted(search);

    Config config = search.getVM().getConfig();
    // Decide whether it is client or server side
    String sideConfig = config.getString("jointstates.side");
    if (sideConfig != null) {
      if (sideConfig.equals("client")) {
        JointstatesListener.side = Side.CLIENT;
        JointstatesListener.otherSide = Side.SERVER;
      } else if (sideConfig.equals("server")) {
        JointstatesListener.side = Side.SERVER;
        JointstatesListener.otherSide = Side.CLIENT;
      } else {
        logger.severe("jointstates.side parameter has invalid value. Allowed values are: [client, server]");
        search.terminate();
      }
    } else {
      logger.severe("jointstates.side parameter is missing. Allowed values are: [client, server]");
      search.terminate();
    }

    CommandDelegator.initialize(config);
    // sendHeartbeatRequest();
  }

  /*
   * (non-Javadoc)
   * @see gov.nasa.jpf.ListenerAdapter#executeInstruction(gov.nasa.jpf.vm.VM,
   * gov.nasa.jpf.vm.ThreadInfo, gov.nasa.jpf.vm.Instruction)
   */
  @Override
  public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
    super.executeInstruction(vm, currentThread, instructionToExecute);

    InstanceInvocation ii;
    int callerRef;
    ElementInfo elementInfo;
    int port;
    if (JointstatesListener.side == Side.CLIENT) {
      // Before Socket.connect()
      if ((instructionToExecute instanceof InstanceInvocation) && (instructionToExecute.getAttr() == JointstatesInstructionFactory.connectFlag)) {
        ii = (InstanceInvocation) instructionToExecute;
        callerRef = ii.getCalleeThis(currentThread);
        elementInfo = vm.getHeap().get(callerRef);
        port = elementInfo.getIntField("port");
        logger.warning("jointstates connect to port " + port);

        // Save current state to continue model checking from this point later
        // PortCollector.addPort(vm.getSearch().getDepth(), port);

        // Reached the next connect() level, backtrack
        // vm.getSearch().setIgnoredState(true);
      }
    }

    if (JointstatesListener.side == Side.SERVER) {
      // ServerSocket.accept()
      if ((instructionToExecute instanceof InstanceInvocation) && (instructionToExecute.getAttr() == JointstatesInstructionFactory.acceptFlag)) {
        ii = (InstanceInvocation) instructionToExecute;
        callerRef = ii.getCalleeThis(currentThread);
        elementInfo = vm.getHeap().get(callerRef);
        port = elementInfo.getIntField("port");
        logger.warning("jointstates accept on port " + port);

        //@formatter:off
        // Save current state to continue model checking from this point later
//        PortCollector.addPort(vm.getSearch().getDepth(), port);
//        vm.breakTransition("because");
//        addApproachedState(vm.getSearch(), port, vm.getRestorableState());

        // Reached the next accept() level, backtrack
//        currentThread.skipInstruction(new NOP());
        //@formatter:on
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see gov.nasa.jpf.ListenerAdapter#instructionExecuted(gov.nasa.jpf.vm.VM,
   * gov.nasa.jpf.vm.ThreadInfo, gov.nasa.jpf.vm.Instruction,
   * gov.nasa.jpf.vm.Instruction)
   */
  @Override
  public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {
    super.instructionExecuted(vm, currentThread, nextInstruction, executedInstruction);

    if (nextInstruction != null) {
      if (nextInstruction.getAttr() == JointstatesInstructionFactory.writeFlag) {
        vm.setNextChoiceGenerator(new BreakGenerator("jointstates before write state", currentThread, false));
        CommandDelegator.lastFlag = nextInstruction.getAttr();
        logger.warning("jointstates added BreakGeneratorCG before write on level " + vm.getSearch().getDepth());
      } else if (nextInstruction.getAttr() == JointstatesInstructionFactory.readFlag) {
        vm.setNextChoiceGenerator(new BreakGenerator("jointstates before read state", currentThread, false));
        CommandDelegator.lastFlag = nextInstruction.getAttr();
        logger.warning("jointstates added BreakGeneratorCG before read on level " + vm.getSearch().getDepth());
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see gov.nasa.jpf.ListenerAdapter#stateStored(gov.nasa.jpf.search.Search)
   */
  @Override
  public void stateStored(Search search) {
    super.stateStored(search);

    logger.warning("jointstates stored state [" + search.getStateId() + "]");
  }

  /*
   * (non-Javadoc)
   * @see
   * gov.nasa.jpf.ListenerAdapter#stateProcessed(gov.nasa.jpf.search.Search)
   */
  @Override
  public void stateProcessed(Search search) {
    super.stateProcessed(search);

    logger.warning("jointstates processed state [" + search.getStateId() + "]");
  }

  @Override
  public void searchFinished(Search search) {
    super.searchFinished(search);
    // try {
    // CommandDelegator.sendMessage(new Message(0, CommandDelegator.getSide(),
    // Side.COMMANDER, MessageType.END));
    CommandDelegator.end();
    JointstatesSearchStateMachine.finish();
    logger.info("jointstates search finished");
    // } catch (InterruptedException e) {
    // logger.severe(e.getMessage());
    // }
  }
}
