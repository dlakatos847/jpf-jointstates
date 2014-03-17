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
package hu.bme.mit.ftsrg.jointstates.collector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class PortCollector {
  private static int currentLevel = 0;

  // level -> ports
  protected static final Map<Integer, Set<Integer>> portsByDepth = new HashMap<Integer, Set<Integer>>();

  static {
    nextLevel();
  }

  public static boolean addPort(int depth, int port) {
    if (!portsByDepth.containsKey(currentLevel)) {
      portsByDepth.put(currentLevel, new HashSet<Integer>());
    }
    return portsByDepth.get(currentLevel).add(port);
  }

  public static int getCurrentLevel() {
    return currentLevel;
  }

  public static void nextLevel() {
    currentLevel++;
    portsByDepth.put(currentLevel, new HashSet<Integer>());
  }

  public static Map<Integer, Set<Integer>> getPortsByDepth() {
    return portsByDepth;
  }

}
