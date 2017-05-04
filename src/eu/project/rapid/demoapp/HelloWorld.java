package eu.project.rapid.demoapp;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.ac.Remoteable;

public class HelloWorld extends Remoteable {
  private static final long serialVersionUID = 389583202049502595L;
  private transient DFE dfe;

  public HelloWorld(DFE dfe) {
    this.dfe = dfe;
  }

  public void helloWorld() {
    Class<?>[] parameterTypes = {};
    try {
      dfe.execute(this.getClass().getDeclaredMethod("rapidhelloWorld", parameterTypes), this);
    } catch (NoSuchMethodException | SecurityException e) {
      System.err.println("Error: " + e);
    }
  }

  @SuppressWarnings("unused")
  private void rapidhelloWorld() {
    System.out.println("Hello World 3 +++++++++++++++++++++++++++++++++++++++++++++++++++!");
  }

  @Override
  public void copyState(Remoteable state) {
    System.out.println("Inside copyState");
  }
}
