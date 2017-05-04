package eu.project.rapid.demoapp;

import java.lang.reflect.Method;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.ac.Remoteable;

public class TestSumNum extends Remoteable {
  private static final long serialVersionUID = 6467207260141507275L;
  private transient DFE dfe;

  public TestSumNum(DFE dfe) {
    this.dfe = dfe;
  }

  public int sumTwoNums(int a, int b) {
    Method toExecute;
    Class<?>[] parameterTypes = {int.class, int.class};
    Object[] parameterValues = {a, b};
    int result = -1;

    try {
      toExecute = this.getClass().getDeclaredMethod("localsumTwoNums", parameterTypes);
      result = (int) dfe.execute(toExecute, parameterValues, this);
    } catch (NoSuchMethodException | SecurityException e) {
      System.err.println("Error: " + e);
    }

    return result;
  }

  @SuppressWarnings("unused")
  private int localsumTwoNums(int a, int b) {
    return a + b;
  }

  @Override
  public void copyState(Remoteable state) {

  }
}
