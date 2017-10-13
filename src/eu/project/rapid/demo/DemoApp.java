package eu.project.rapid.demo;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.common.RapidConstants.ExecLocation;
import eu.project.rapid.demo.gvirtus.MatrixMul;
import eu.project.rapid.demo.helloJNI.HelloJNI;
import eu.project.rapid.demo.nqueens.NQueens;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class DemoApp {
    private DFE dfe;

    private final static Logger log = LogManager.getLogger(DemoApp.class.getSimpleName());

    private ExecLocation[] execLocations = {ExecLocation.LOCAL, ExecLocation.REMOTE};

    // Variables for statistics
    private int[] nrQueens = {4, 5, 6, 7, 8};
    private int[] nrTestsQueens = {5, 5, 5, 5, 5};
    private double[] nQueensLocalDur = new double[nrQueens.length];
    private int[] nQueensLocalNr = new int[nrQueens.length];
    private double[] nQueensRemoteDur = new double[nrQueens.length];
    private int[] nQueensRemoteNr = new int[nrQueens.length];

    private int nrJniTests = 5;
    private double jniLocalDur;
    private int jniLocalNr;
    private double jniRemoteDur;
    private int jniRemoteNr;

    private int nrCudaTests = 1;
    private double cudaLocalDur;
    private int cudaLocalNr;
    private double cudaRemoteDur;
    private int cudaRemoteNr;

    public DemoApp(String vmIP, String connType) {

        if (vmIP != null) {
            log.info("Registering with the given VM...");
            dfe = DFE.getInstance(vmIP);
        } else {
            log.info("Registering with the RAPID system to get a VM...");
            dfe = DFE.getInstance();
        }

        dfe.setUserChoice(ExecLocation.REMOTE);
        dfe.setConnEncrypted(connType != null && connType.equals("ssl"));

        System.out.println();
        System.out.println();
//        log.info("Testing JNI...");
//        testHelloJni();

        System.out.println();
        System.out.println();
        log.info("Testing NQueens...");
        testNQueens();

        System.out.println();
        System.out.println();
//        log.info("Testing CUDA offloading...");
//        testCUDA();

        dfe.destroy();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("---------------- Cumulative statistics of the demo testing applications ----------------");

        // nQueens
        for (int i = 0; i < nrQueens.length; i++) {
            System.out.println();
            log.debug(nrQueens[i] + "-Queens");
            System.out.printf("%12s %15s%n", "Nr.", "Avg. dur. (ms)");
            System.out.printf("%-8s %3d %15.2f%n", "Local", nQueensLocalNr[i], nQueensLocalDur[i] / nQueensLocalNr[i] / 1000000);
            System.out.printf("%-8s %3d %15.2f%n", "Remote", nQueensRemoteNr[i], nQueensRemoteDur[i] / nQueensRemoteNr[i] / 1000000);
        }

        System.out.println();
        log.debug("jni hello");
        System.out.printf("%12s %15s%n", "Nr.", "Avg. dur. (ms)");
        System.out.printf("%-8s %3d %15.2f%n", "Local", jniLocalNr, jniLocalDur / jniLocalNr / 1000000);
        System.out.printf("%-8s %3d %15.2f%n", "Remote", jniRemoteNr, jniRemoteDur / jniRemoteNr / 1000000);

        System.out.println();
        log.debug("CUDA MatrixMul");
        System.out.printf("%12s %15s%n", "Nr.", "Avg. dur. (ms)");
        System.out.printf("%-8s %3d %15.2f%n", "Local", cudaLocalNr, cudaLocalDur / cudaLocalNr / 1000000);
        System.out.printf("%-8s %3d %15.2f%n", "Remote", cudaRemoteNr, cudaRemoteDur / cudaRemoteNr / 1000000);

    }

    private void testNQueens() {
        NQueens q = new NQueens(dfe);

        for (ExecLocation execLocation : execLocations) {
            dfe.setUserChoice(execLocation);
            for (int i = 0; i < nrQueens.length; i++) {
                for (int j = 0; j < nrTestsQueens[i]; j++) {
                    int result = q.solveNQueens(nrQueens[i]);
                    log.info("Result of NQueens(" + nrQueens[i] + "): " + result);

                    String methodName = "localSolveNQueens";
                    if (dfe.getLastExecLocation(methodName).equals(ExecLocation.LOCAL)) {
                        nQueensLocalNr[i]++;
                        nQueensLocalDur[i] += dfe.getLastExecDuration(methodName);
                    } else {
                        nQueensRemoteNr[i]++;
                        nQueensRemoteDur[i] += dfe.getLastExecDuration(methodName);
                    }
                }
            }
        }
    }

    private void testHelloJni() {
        HelloJNI helloJni = new HelloJNI(dfe);

        for (ExecLocation execLocation : execLocations) {
            dfe.setUserChoice(execLocation);
            for (int i = 0; i < nrJniTests; i++) {
                int result = helloJni.printJava();
                log.info("The result of the native call with DFE: " + result);

                String methodName = "rapidprintJava";
                if (dfe.getLastExecLocation(methodName).equals(ExecLocation.LOCAL)) {
                    jniLocalNr++;
                    jniLocalDur += dfe.getLastExecDuration(methodName);
                } else {
                    jniRemoteNr++;
                    jniRemoteDur += dfe.getLastExecDuration(methodName);
                }
            }
        }
    }

    private void testCUDA() {
        MatrixMul matrixMul = null;
        try {
            matrixMul = new MatrixMul(dfe);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int wa = 8;
        int wb = 12;

        for (ExecLocation execLocation : execLocations) {
            dfe.setUserChoice(execLocation);
            for (int i = 0; i < nrCudaTests; i++) {
                log.info("------------ Started running CUDA MatrixMul with DFE.");
                matrixMul.gpuMatrixMul(wa, wb, wa);
                log.info("Finished executing CUDA MatrixMul with DFE.");

                String methodName = "localGpuMatrixMul";
                if (dfe.getLastExecLocation(methodName).equals(ExecLocation.LOCAL)) {
                    cudaLocalNr++;
                    cudaLocalDur += dfe.getLastExecDuration(methodName);
                } else {
                    cudaRemoteNr++;
                    cudaRemoteDur += dfe.getLastExecDuration(methodName);
                }
            }
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv) {
        String vmIP = "192.168.0.104";
        String connType = "ssl";

        for (int i = 0; i < argv.length; i++) {
            switch (argv[i]) {
                case "-vm":
                    vmIP = argv[i + 1];
                    break;

                case "-rapid":
                    vmIP = null;
                    break;

                case "-conn":
                    connType = argv[i + 1];
                    break;
            }
        }

        log.info("Creating connection with VM: " + vmIP);
        new DemoApp(vmIP, connType);
        System.exit(0);
    }
}
