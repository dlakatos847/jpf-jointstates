package hu.bme.mit.ftsrg.jointstates.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerPortCollector {
  /*
   * Singleton
   */
  private static ServerPortCollector spc = new ServerPortCollector();

  private int currentLevel = 1;
  private final Map<Integer, Set<Integer>> listeningPorts = new HashMap<Integer, Set<Integer>>();

  public static boolean addListeningPort(int port) {
    if (!spc.listeningPorts.containsKey(spc.currentLevel)) {
      spc.listeningPorts.put(spc.currentLevel, new HashSet<Integer>());
    }
    return spc.listeningPorts.get(spc.currentLevel).add(port);
  }

  public static Set<Integer> getListeningPorts() {
    return spc.listeningPorts.get(spc.currentLevel);
  }

  public static int getCurrentLevel() {
    return spc.currentLevel;
  }

  public static void nextLevel() {
    spc.currentLevel++;
  }
}
