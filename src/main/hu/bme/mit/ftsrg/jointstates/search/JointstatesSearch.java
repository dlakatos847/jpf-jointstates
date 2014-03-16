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
import gov.nasa.jpf.search.DFSearch;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.VM;
import hu.bme.mit.ftsrg.jointstates.collector.ApproachedState;
import hu.bme.mit.ftsrg.jointstates.collector.BfsState;
import hu.bme.mit.ftsrg.jointstates.collector.FakeServer;
import hu.bme.mit.ftsrg.jointstates.collector.StateCollector;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesSearch extends Search {
  protected static final Logger logger = JPF.getLogger(JointstatesSearch.class.getCanonicalName());
  public static final Object clientSide = new Object();
  public static final Object serverSide = new Object();
  public static Object side = null;

  protected DFSearch dfs;

  /**
   * @param config
   * @param vm
   */
  public JointstatesSearch(Config config, VM vm) {
    super(config, vm);

    // Decide whether it is client or server side
    String sideConfig = config.getString("jointstates.side");
    if (sideConfig != null) {
      if (sideConfig.equals("client")) {
        JointstatesSearch.side = JointstatesSearch.clientSide;
      } else if (sideConfig.equals("server")) {
        JointstatesSearch.side = JointstatesSearch.serverSide;
      } else {
        logger.severe("jointstates.side parameter has invalid value. Allowed values are: [client, server]");
        terminate();
      }
    } else {
      logger.severe("jointstates.side parameter is missing. Allowed values are: [client, server]");
      terminate();
    }

    // Initialize command subsystem
    // TODO
  }

  /*
   * (non-Javadoc)
   * @see gov.nasa.jpf.search.Search#search()
   */
  @Override
  public void search() {
    BfsState bfsState = null;
    this.depth = 0;

    notifySearchStarted();

    // add the root state to the queue as the first restorable state
    StateCollector.addBfsState(new BfsState(this.depth, 0, this.vm.getRestorableState(), null));

    // main BFS search loop
    while ((bfsState = StateCollector.getBfsState()) != null && !this.done) {
      // load the next restorable state
      this.vm.restoreState(bfsState.getState());
      this.depth = bfsState.getDepth();

      logger.info("jointstates search on depth " + this.depth);

      // explore the approachable states
      this.dfs = new DFSearch(this.config, this.vm);
      this.dfs.search();

      if (this.dfs.hasErrors()) {
        terminate();
      }

      if (JointstatesSearch.side == JointstatesSearch.clientSide) {
        // Get the message with a fake server on the client's connect port
        ApproachedState approachedState;
        while ((approachedState = StateCollector.getApproachedState()) != null) {
          FakeServer fs = new FakeServer(approachedState.getPort());

          // TODO connect the client to the fake server

          // TODO return the message from the fake server

        }
      } else {
        // The client's messages should branch the server's state space
      }
    }

    notifySearchFinished();
  }

  @Override
  public boolean supportsBacktrack() {
    return true;
  }

  @Override
  public boolean supportsRestoreState() {
    // this is the point
    return true;
  }
}
