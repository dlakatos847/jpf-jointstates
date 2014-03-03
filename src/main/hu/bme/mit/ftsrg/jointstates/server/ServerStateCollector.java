package hu.bme.mit.ftsrg.jointstates.server;

import gov.nasa.jpf.vm.RestorableVMState;

import java.util.LinkedList;
import java.util.Queue;

public class ServerStateCollector {
  private static ServerStateCollector ssc = new ServerStateCollector();

  private final Queue<RestorableVMState> restorableServerStates = new LinkedList<RestorableVMState>();

  public static void addServerState(RestorableVMState state) {
    ssc.restorableServerStates.add(state);
  }

  public static RestorableVMState getServerState() {
    return ssc.restorableServerStates.poll();
  }
}
