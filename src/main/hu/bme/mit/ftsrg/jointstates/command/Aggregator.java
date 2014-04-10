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

import java.io.IOException;
import java.io.InputStream;
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
public class Aggregator extends Thread {

  public enum AggregatorType {
    ADD, QUERY
  }

  // parent JointStateId -> transitions
  private static Map<Integer, Set<JointStateTransition>> jointStateTransitions = new HashMap<Integer, Set<JointStateTransition>>();
  private static int nextJointStateId = 1;
  private static Map<AggregatorType, Integer> aggregatorTypeListenPort = new HashMap<AggregatorType, Integer>();

  static {
    aggregatorTypeListenPort.put(AggregatorType.ADD, 8081);
    aggregatorTypeListenPort.put(AggregatorType.QUERY, 8082);

    jointStateTransitions.put(0, new HashSet<JointStateTransition>());
  }

  private AggregatorType type;

  public Aggregator(AggregatorType type) {
    this.type = type;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void runAdd(Socket s) throws IOException {
    InputStream is = s.getInputStream();
    OutputStream os = s.getOutputStream();
    int lastJointStateId = is.read();
    int message = is.read();
    JointStateTransition newJst = new JointStateTransition(message, nextJointStateId);

    synchronized (jointStateTransitions) {
      // the parent JointState must already exist
      assert jointStateTransitions.containsKey(lastJointStateId);

      // add message to the possible JointState transitions
      Set<JointStateTransition> jstSet = jointStateTransitions.get(lastJointStateId);
      if (jstSet.add(newJst)) {
        // create a new parent for further transitions
        jointStateTransitions.put(nextJointStateId, new HashSet<JointStateTransition>());
        os.write(nextJointStateId);
        System.out.println("[" + lastJointStateId + "] Added new transition\tmessage: " + message + "\tnext JointState ID: " + nextJointStateId);
        nextJointStateId++;
      } else {
        Iterator<JointStateTransition> iter = jstSet.iterator();
        JointStateTransition exJst = null;
        while (iter.hasNext()) {
          exJst = iter.next();
          if (exJst.equals(newJst)) {
            os.write(exJst.getNextJointStateId());
            break;
          }
        }
        System.out.println("[" + lastJointStateId + "] Transition already stored\tmessage " + message + "\tnext JointState ID: " + exJst.getNextJointStateId());
      }
    }
  }

  private void runQuery(Socket s) throws IOException {
    InputStream is = s.getInputStream();
    OutputStream os = s.getOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(os);
    int lastJointStateId = is.read();
    synchronized (jointStateTransitions) {
      Set<JointStateTransition> jsts = jointStateTransitions.get(lastJointStateId);
      JointStateTransition jstsArray[] = new JointStateTransition[jsts.size()];
      jsts.toArray(jstsArray);
      oos.writeObject(jstsArray);
    }
    System.out.println("[" + lastJointStateId + "] Queried transitions");
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Aggregator addMessageAggregator = new Aggregator(AggregatorType.ADD);
    Aggregator queryMessageAggregator = new Aggregator(AggregatorType.QUERY);

    addMessageAggregator.start();
    queryMessageAggregator.start();
  }

}
