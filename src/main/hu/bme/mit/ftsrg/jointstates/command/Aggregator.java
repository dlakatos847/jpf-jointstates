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
import java.io.ObjectOutputStream;
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

  private static Object sync = new Object();
  private static Map<Integer, Set<Integer>> messageOnLevel = new HashMap<Integer, Set<Integer>>();
  private static Map<AggregatorType, Integer> aggregatorTypeListenPort = new HashMap<AggregatorType, Integer>();

  static {
    aggregatorTypeListenPort.put(AggregatorType.ADD, 8081);
    aggregatorTypeListenPort.put(AggregatorType.QUERY, 8082);
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
          } else {
            System.err.println("Aggregator type problem");
            System.exit(-1);
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
    int jointDepth = s.getInputStream().read();
    int message = s.getInputStream().read();
    synchronized (Aggregator.sync) {
      // create new joint depth if doesn't exist
      if (!messageOnLevel.containsKey(jointDepth)) {
        messageOnLevel.put(jointDepth, new HashSet<Integer>());
        System.out.println("Added new level: " + jointDepth);
      }
      // add message to joint depth
      if (messageOnLevel.get(jointDepth).add(message)) {
        System.out.println("[" + jointDepth + "] Added new message " + message);
      } else {
        System.out.println("[" + jointDepth + "] Message already stored " + message);
      }
    }
  }

  private void runQuery(Socket s) throws IOException {
    int jointDepth = s.getInputStream().read();
    synchronized (Aggregator.sync) {
      Set<Integer> messages = messageOnLevel.get(jointDepth);
      int[] messagesArray = new int[messages.size()];
      Iterator<Integer> i = messages.iterator();
      int j = 0;
      while (i.hasNext()) {
        messagesArray[j] = i.next();
        j++;
      }
      ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
      os.writeObject(messagesArray);
      System.out.print("[" + jointDepth + "] Queried messages. Replied: ");
      for (int k : messagesArray) {
        System.out.print(k + " ");
      }
      System.out.print("\n");
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Aggregator addMessageAggregator = new Aggregator(AggregatorType.ADD);
    Aggregator queryMessageAggregator = new Aggregator(AggregatorType.QUERY);
    addMessageAggregator.start();
    queryMessageAggregator.start();
  }

}
