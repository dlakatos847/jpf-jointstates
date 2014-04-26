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
package hu.bme.mit.ftsrg.jointstates.command;

import hu.bme.mit.ftsrg.jointstates.core.Side;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class Aggregator implements Runnable {

  // parent JointStateId -> transitions
  private static Map<Integer, Set<JointStateTransition>> clientTransitions = new HashMap<Integer, Set<JointStateTransition>>();
  private static Map<Integer, Set<JointStateTransition>> serverTransitions = new HashMap<Integer, Set<JointStateTransition>>();
  private static int nextJointStateId = 1;
  private static Map<AggregatorType, Integer> aggregatorTypeListenPort = new HashMap<AggregatorType, Integer>();
  public static int AGGREGATOR_ADD_PORT = 8081;
  public static int AGGREGATOR_QUERY_PORT = 8082;

  static {
    aggregatorTypeListenPort.put(AggregatorType.ADD, AGGREGATOR_ADD_PORT);
    aggregatorTypeListenPort.put(AggregatorType.QUERY, AGGREGATOR_QUERY_PORT);

    // clientTransitions.put(0, new HashSet<JointStateTransition>());
    // serverTransitions.put(0, new HashSet<JointStateTransition>());
  }

  private AggregatorType type;

  /**
   * C'tor
   * 
   * @param type
   *          Defines whether it is an ADD or QUERY type Aggregator
   */
  public Aggregator(AggregatorType type) {
    this.type = type;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    System.out.println("jointstates Aggregator " + this.type + " started");

    try {
      ServerSocket ss = new ServerSocket(Aggregator.aggregatorTypeListenPort.get(this.type));
      while (!Thread.interrupted()) {
        Socket s = null;
        try {
          s = ss.accept();
          if (this.type == AggregatorType.ADD) {
            runAdd(s);
          } else if (this.type == AggregatorType.QUERY) {
            runQuery(s);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (s != null) {
            try {
              s.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
      ss.close();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void runAdd(Socket s) throws IOException, ClassNotFoundException {
    InputStream is = s.getInputStream();
    ObjectInputStream ois = new ObjectInputStream(is);
    OutputStream os = s.getOutputStream();
    // ObjectOutputStream oos = new ObjectOutputStream(os);
    int parentJointStatId;

    // networking
    parentJointStatId = is.read();
    Side destination = (Side) ois.readObject();
    int message = is.read();

    // generating the possible transition to add
    JointStateTransition newTransition = new JointStateTransition(message, nextJointStateId);

    // selecting the approrpiate map
    Map<Integer, Set<JointStateTransition>> actualTransitions = (destination == Side.CLIENT ? Aggregator.clientTransitions : Aggregator.serverTransitions);

    synchronized (actualTransitions) {
      // Create the record if does not exist
      if (!actualTransitions.containsKey(parentJointStatId)) {
        actualTransitions.put(parentJointStatId, new HashSet<JointStateTransition>());
        System.out.println("[" + parentJointStatId + "] Created empty container for transitions " + destination);
      }

      // add message to the possible JointState transitions
      Set<JointStateTransition> existingTransitions = actualTransitions.get(parentJointStatId);

      // The transition is not in the set (added successfully)
      // * add it to the set
      // * return the new joint state id
      if (existingTransitions.add(newTransition)) {
        // create a new parent for further transitions
        os.write(nextJointStateId);
        System.out.println("[" + parentJointStatId + "] Added new transition " + newTransition + " to " + destination);
        nextJointStateId++;
      }
      // The transition is already in the set
      // TODO should use something like Google Guava to look for the transition
      // by predicates (more elegant)
      // * search for the identical transition
      // * return its next joint state id
      else {
        Iterator<JointStateTransition> transitionIterator = existingTransitions.iterator();
        JointStateTransition actualTransition = null;
        while (transitionIterator.hasNext()) {
          actualTransition = transitionIterator.next();
          if (actualTransition.equals(newTransition)) {
            os.write(actualTransition.getNextJointStateId());
            break;
          }
        }
        System.out.println("[" + parentJointStatId + "] Transition already stored " + actualTransition);
      }
    }
  }

  private void runQuery(Socket s) throws IOException, ClassNotFoundException {
    InputStream is = s.getInputStream();
    ObjectInputStream ois = new ObjectInputStream(is);
    OutputStream os = s.getOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(os);
    int parentJointStateId;
    Side destination;

    // networking
    parentJointStateId = is.read();
    destination = (Side) ois.readObject();

    // selecting the approrpiate map
    Map<Integer, Set<JointStateTransition>> actualTransitions = (destination == Side.CLIENT ? Aggregator.clientTransitions : Aggregator.serverTransitions);

    Set<JointStateTransition> transitions;
    synchronized (actualTransitions) {
      transitions = actualTransitions.get(parentJointStateId);
      JointStateTransition transitionArray[] = new JointStateTransition[transitions.size()];
      transitions.toArray(transitionArray);
      oos.writeObject(transitionArray);
    }
    System.out.println("[" + parentJointStateId + "] Queried transitions from " + destination + " " + transitions);
  }

}
