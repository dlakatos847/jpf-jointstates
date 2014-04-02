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
import gov.nasa.jpf.search.heuristic.SimplePriorityHeuristic;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;
import hu.bme.mit.ftsrg.jointstates.core.JointstatesInstructionFactory;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesHeuristicSearch extends SimplePriorityHeuristic {

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
    //@formatter:off
    Instruction insn = this.getVM().getCurrentThread().getPC();
    boolean isConnect = insn == null ? false : insn.getAttr() == JointstatesInstructionFactory.connectFlag;
    boolean isAccept = insn == null ? false : insn.getAttr()  == JointstatesInstructionFactory.acceptFlag;

    if (isConnect || isAccept) {
      return Integer.MAX_VALUE;
        }
    //@formatter:on

    // -1 is because we would like to avoid priority collisions between the
    // original DFS states and the 'interesting' joint states

    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
    return Integer.MAX_VALUE - this.vm.getPathLength() - 1;
  }
}
