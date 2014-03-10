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
import hu.bme.mit.ftsrg.jointstates.collector.PortCollector;
import hu.bme.mit.ftsrg.jointstates.collector.StateCollector;
import hu.bme.mit.ftsrg.jointstates.core.JointstatesInstructionFactory;

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
  public static final Object clientSide = new Object();
  public static final Object serverSide = new Object();

  protected Object side = null;

  /*
   * Make sure jpf-jointstates master-JPF is available (non-Javadoc)
   * @see gov.nasa.jpf.ListenerAdapter#searchStarted(gov.nasa.jpf.search.Search)
   */
  @Override
  public void searchStarted(Search search) {
    super.searchStarted(search);

    Config config = search.getVM().getConfig();
    String sideConfig = config.getString("jointstates.side");

    // jointstates.side missing
    if (sideConfig == null) {
      logger.severe("jointstates.side parameter is missing. Allowed values are: [client, server]");
      search.terminate();
    }

    if (sideConfig.equals("client")) {
      this.side = JointstatesListener.clientSide;
    } else if (sideConfig.equals("server")) {
      this.side = JointstatesListener.serverSide;
    } else {
      logger.severe("jointstates.side parameter has invalid value. Allowed values are: [client, server]");
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

    if (this.side == clientSide) {
      // Socket ctor
      if ((instructionToExecute instanceof InstanceInvocation) && (instructionToExecute.getAttr() == JointstatesInstructionFactory.connectFlag)) {
        InstanceInvocation ii = (InstanceInvocation) instructionToExecute;
        // Save current state to continue model checking from this point later
        StateCollector.addClientState(vm.getRestorableState());
        int callerRef = ii.getCalleeThis(currentThread);
        ElementInfo socketElementInfo = vm.getHeap().get(callerRef);
        int port = socketElementInfo.getIntField("port");
        logger.info("connect to port " + port);
        PortCollector.addPort(port);

        // Reached the next connect() level, backtrack
        vm.getSearch().setIgnoredState(true);
      }
    }

    if (this.side == serverSide) {
      // ServerSocket.accept()
      if ((instructionToExecute instanceof InstanceInvocation) && (instructionToExecute.getAttr() == JointstatesInstructionFactory.acceptFlag)) {
        InstanceInvocation ii = (InstanceInvocation) instructionToExecute;
        // Save current state to continue model checking from this point later
        StateCollector.addServerState(vm.getRestorableState());
        int callerRef = ii.getCalleeThis(currentThread);
        ElementInfo serverSocketElementInfo = vm.getHeap().get(callerRef);
        int port = serverSocketElementInfo.getIntField("port");
        logger.info("accept on port " + port);
        PortCollector.addPort(port);

        // Reached the next accept() level, backtrack
        vm.getSearch().setIgnoredState(true);
      }
    }
  }

  @Override
  public void searchFinished(Search search) {
    super.searchFinished(search);
    if (this.side == clientSide) {
      logger.info("Restorable client states: " + StateCollector.getClientStateSize());
      for (int i : PortCollector.getPorts()) {
        logger.info("connect port: " + i);
      }
    } else {
      logger.info("restorable server states: " + StateCollector.getServerStateSize());
      for (int i : PortCollector.getPorts()) {
        logger.info("accept port: " + i);
      }
    }

    // search.getVM().restoreState(StateCollector.getServerState());
  }

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
