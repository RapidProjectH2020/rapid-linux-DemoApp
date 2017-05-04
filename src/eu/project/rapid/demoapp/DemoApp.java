package eu.project.rapid.demoapp;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.common.RapidConstants.ExecLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DemoApp {
  private DFE dfe;

  private final static Logger log = LogManager.getLogger(DemoApp.class.getSimpleName());

  public DemoApp() {

    // dfe = DFE.getInstance();
    // dfe = DFE.getInstance("10.0.0.13");
    dfe = DFE.getInstance("54.216.218.142");
    dfe.setUserChoice(ExecLocation.REMOTE);

    // log.info("Testing JNI...");
    // testHelloJni();

    log.info("Testing HelloWorld...");
    testHelloWorld();
    //
    // log.info("Testing Sum of two numbers...");
    // testSumNum();
    //
    // log.info("Testing NQueens...");
    // testNQueens();

    log.info("Testing overhead of connection, UL, and DL with SSL and CLEAR");

    dfe.destroy();

    // testGvirtus();
  }

  private void testHelloWorld() {
    HelloWorld h = new HelloWorld(dfe);
    h.helloWorld();
  }

  private void testHelloJni() {
    HelloJNI helloJni = new HelloJNI(dfe);
    int nrTests = 1;
    for (int i = 0; i < nrTests; i++) {
      int result = helloJni.printJava();
      log.info("The result of the native call: " + result);
    }
  }

  private void testSumNum() {
    TestSumNum t = new TestSumNum(dfe);
    int a = 3, b = 5;
    int result = t.sumTwoNums(a, b);
    log.info("Result of sum of two nums test: " + a + " + " + b + " = " + result);
  }

  private void testNQueens() {

    NQueens q = new NQueens(dfe);
    int result = -1;
    int[] nrQueens = {4, 5, 6, 7, 8};
    int[] nrTests = {1, 1, 1, 1, 1};

    for (int i = 0; i < nrQueens.length; i++) {
      for (int j = 0; j < nrTests[i]; j++) {
        result = q.solveNQueens(nrQueens[i]);
      }
      log.info("Result of NQueens(" + nrQueens[i] + "): " + result);
    }
  }

  private void testGvirtus() {
    new GVirtusDemo(dfe);
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] argv) {
    new DemoApp();
  }
}
