package eu.project.rapid.demo;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.common.RapidConstants.ExecLocation;
import eu.project.rapid.demo.gvirtus.MatrixMul;
import eu.project.rapid.demo.helloJNI.HelloJNI;
import eu.project.rapid.demo.nqueens.NQueens;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DemoApp {
    private DFE dfe;

    private final static Logger log = LogManager.getLogger(DemoApp.class.getSimpleName());

    public DemoApp(String vmIP) {

        if (vmIP != null) {
            log.info("Registering with the given VM...");
            dfe = DFE.getInstance(vmIP);
        } else {
            log.info("Registering with the RAPID system to get a VM...");
            dfe = DFE.getInstance();
        }

        dfe.setUserChoice(ExecLocation.REMOTE);
//        dfe.setUserChoice(ExecLocation.LOCAL);

        System.out.println();
        System.out.println();
        log.info("Testing JNI...");
        testHelloJni();

        /*
        System.out.println();
        System.out.println();
        log.info("Testing NQueens...");
        testNQueens();

        System.out.println();
        System.out.println();
        log.info("Testing CUDA offloading...");
        testGvirtus();
        */

        dfe.destroy();
    }

    private void testHelloJni() {
        int nrTests = 1;
        HelloJNI helloJni = new HelloJNI(dfe);
        for (int i = 0; i < nrTests; i++) {
            int result = helloJni.printJava();
            log.info("The result of the native call with DFE: " + result);
        }
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
        int nrTests = 1;
        MatrixMul matrixMul = new MatrixMul(dfe);
        int wa = 8;
        int wb = 12;

        for (int i = 0; i < nrTests; i++) {
            log.info("------------ Started running GVirtuS with DFE.");
            matrixMul.gpuMatrixMul(wa, wb, wa);
            log.info("Finished executing GVirtuS matrixMul with DFE.");
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

        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals("-vm")) {
                vmIP = argv[i + 1];
            }
            if (argv[i].equals("-rapid")) {
                vmIP = null;
                break;
            }
        }

        log.info("Creating connection with VM: " + vmIP);
        new DemoApp(vmIP);
    }
}
