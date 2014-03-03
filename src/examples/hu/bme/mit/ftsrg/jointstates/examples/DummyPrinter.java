package hu.bme.mit.ftsrg.jointstates.examples;

public class DummyPrinter implements Runnable {
  private final String str;

  public DummyPrinter(String str) {
    this.str = str;
  }

  public static void main(String[] args) {
    new Thread(new DummyPrinter("A")).start();
    // new Thread(new DummyPrinter("B")).start();
  }

  @Override
  public void run() {
    System.out.println(this.str);
  }
}
