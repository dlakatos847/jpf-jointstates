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
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.VM;
import hu.bme.mit.ftsrg.jointstates.collector.ApproachedState;
import hu.bme.mit.ftsrg.jointstates.collector.StateCollector;

import java.util.logging.Logger;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesSearch extends Search {
  protected static final Logger logger = JPF.getLogger(JointstatesSearch.class.getCanonicalName());
  protected ApproachedState approachedState = null;
  private int bfsDepth = 0;

  /**
   * @param config
   * @param vm
   */
  public JointstatesSearch(Config config, VM vm) {
    super(config, vm);

    // Initialize command subsystem
    // TODO
  }

  /*
   * (non-Javadoc)
   * @see gov.nasa.jpf.search.Search#search()
   */
  @Override
  public void search() {
    this.depth = 0;
    notifySearchStarted();

    // add the root state to the queue as the first restorable state
    StateCollector.addApproachedState(new ApproachedState(this.bfsDepth, 0, this.vm.getRestorableState()));

    notifySearchStarted();

    while ((this.approachedState = StateCollector.getApproachedState()) != null) {
      this.vm.restoreState(this.approachedState.getState());
      while (true) {
        if (checkAndResetBacktrackRequest() || !isNewState() || isEndState() || isIgnoredState()) {
          if (!backtrack()) { // backtrack not possible, done
            break;
          }

          this.depth--;
          notifyStateBacktracked();
        }

        if (forward()) {
          this.depth++;
          notifyStateAdvanced();

          if (this.currentError != null) {
            notifyPropertyViolated();

            if (hasPropertyTermination()) {
              break;
            }
          }

        } else { // forward did not execute any instructions
          notifyStateProcessed();
        }
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
