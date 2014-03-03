package hu.bme.mit.ftsrg.jointstates.listener;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.RestorableVMState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JointstatesInterveningListener extends ListenerAdapter {
	static Logger logger = JPF.getLogger(JointstatesInterveningListener.class
			.getCanonicalName());
	static List<RestorableVMState> restorableStates = new ArrayList<RestorableVMState>();

	/*
	 * Make sure jpf-jointstates master-JPF is available (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.jpf.ListenerAdapter#searchStarted(gov.nasa.jpf.search.Search)
	 */
	@Override
	public void searchStarted(Search search) {
		// TODO Auto-generated method stub
		// sendHeartbeatRequest();
		super.searchStarted(search);
	}

	@Override
	public void executeInstruction(VM vm, ThreadInfo currentThread,
			Instruction instructionToExecute) {
		// TODO Auto-generated method stub
		// if(instructionToExecute instanceof gov.nasa.jpf.jvm.bytecode.)
		super.executeInstruction(vm, currentThread, instructionToExecute);
	}

	@Override
	public void choiceGeneratorSet(VM vm, ChoiceGenerator<?> newCG) {
		super.choiceGeneratorSet(vm, newCG);
		restorableStates.add(vm.getRestorableState());
	}

	@Override
	public void searchFinished(Search search) {
		super.searchFinished(search);
		logger.info("Restorable states: " + restorableStates.size());
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
