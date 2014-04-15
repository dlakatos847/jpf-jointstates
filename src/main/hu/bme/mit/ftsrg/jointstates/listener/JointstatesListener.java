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
import hu.bme.mit.ftsrg.jointstates.collector.PortCollector;
import hu.bme.mit.ftsrg.jointstates.command.CommandDelegator;
import hu.bme.mit.ftsrg.jointstates.core.JointstatesInstructionFactory;
import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesListener extends ListenerAdapter {
  protected static final Logger logger = JPF.getLogger(JointstatesListener.class.getCanonicalName());
  public static Side side;

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
      } else if (sideConfig.equals("server")) {
        JointstatesListener.side = Side.SERVER;
      } else {
        logger.severe("jointstates.side parameter has invalid value. Allowed values are: [client, server]");
        search.terminate();
      }
    } else {
      logger.severe("jointstates.side parameter is missing. Allowed values are: [client, server]");
      search.terminate();
    }

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
        logger.info("connect to port " + port);

        // Save current state to continue model checking from this point later
        PortCollector.addPort(vm.getSearch().getDepth(), port);

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
        logger.info("accept on port " + port);

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

    if (JointstatesListener.side == Side.CLIENT) {
      // After Socket.connect()
      if ((executedInstruction instanceof InstanceInvocation) && (executedInstruction.getAttr() == JointstatesInstructionFactory.connectFlag)) {

      }
    }

    if (nextInstruction != null) {
      if (nextInstruction.getAttr() == JointstatesInstructionFactory.readFlag || nextInstruction.getAttr() == JointstatesInstructionFactory.writeFlag) {
        vm.setNextChoiceGenerator(new BreakGenerator("r/w before state", currentThread, false));
        CommandDelegator.lastFlag = nextInstruction.getAttr();
        logger.info("jointstates added BreakGeneratorCG on level " + vm.getSearch().getDepth());
      }
    }
  }

  // @formatter:off
//  @Override
//  public void searchFinished(Search search) {
//    super.searchFinished(search);
//    Map<Integer, Set<Integer>> portsByDepth = PortCollector.getPortsByDepth();
//    Iterator<Integer> iter;
//    for (int i = 1; i <= search.getDepth(); ++i) {
//      iter = portsByDepth.get(i).iterator();
//      if (JointstatesSearch.side == Side.CLIENT) {
//        while (iter.hasNext()) {
//          logger.info("depth: " + search.getDepth() + ", connect port: " + iter.next());
//        }
//      } else if (JointstatesSearch.side == Side.SERVER) {
//        while (iter.hasNext()) {
//          logger.info("depth: " + search.getDepth() + ", accept port: " + iter.next());
//        }
//      }
//
//      // search.getVM().restoreState(StateCollector.getServerState());
//    }
//  }
  // @formatter:on

  public void sendHeartbeatRequest() {
    String hostName = "127.0.0.1";
    Socket socket = null;
    OutputStream outStream = null;
    try {
      InetAddress addr = InetAddress.getByName(hostName);
      int portNumber = 7000;
      socket = new Socket(addr, portNumber);
      outStream = socket.getOutputStream();
      outStream.write(99);
      if (socket.getInputStream().read() == 100) {
        logger.info("GOOD HEARTBEAT RESPONSE");
      } else {
        logger.severe("WRONG HEARTBEAT RESPONSE");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
