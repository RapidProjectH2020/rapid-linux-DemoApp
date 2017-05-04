package eu.project.rapid.demoapp;

import java.lang.reflect.Method;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.ac.Remoteable;
import eu.project.rapid.utils.Utils;

public class HelloJNI extends Remoteable {

  private static final long serialVersionUID = -5942880824910953975L;
  private transient DFE dfe;

  static {
    try {
      if (Utils.isMac()) {
        System.loadLibrary("HelloJNI");
      } else if (Utils.isLinux()) {
        System.loadLibrary("hellojni");
        // System.err.println("Sokol: library not compiled for Linux.");
      } else if (Utils.isWindows()) {
        System.err.println("Sokol: library not compiled for Windows.");
      }
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Could not load native library, maybe this is running on the clone.");
      e.printStackTrace();
    }

    try {
      System.out.println("Loading HandTrackerJNI...");
      System.loadLibrary("HandTrackerJNI");
      System.out.println("HandTrackerJNI loaded.");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Could not load HandTrackerJNI.");
    }
  }

  public HelloJNI(DFE dfe) {
    this.dfe = dfe;
  }

  /**
   * A native method implemented in C++
   */
  public native int print();

  public int printJava() {
    int result = 0;
    Class<?>[] parameterTypes = {};
    Method method;
    try {
      method = this.getClass().getMethod("rapidprintJava", parameterTypes);
      result = (int) dfe.execute(method, this);
    } catch (NoSuchMethodException | SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return result;
  }

  public int rapidprintJava() {
    return print();
  }

  @Override
  public void copyState(Remoteable state) {}
}
