package hu.bme.mit.ftsrg.jointstates;

public class Jointstates {

  public static void main(String[] args) {
    HeartbeatResponder hr = new HeartbeatResponder(7000);
    hr.start();

  }
}
