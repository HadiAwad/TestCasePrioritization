package salp;

import Catalano.Core.IntRange;
import salp.IEvoIterationListener;
import utils.APFDObjectiveFunction;
import utils.FitnessBasedOnTime;
import utils.IObjectiveFunction;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SSARunner {

    public static void main(String[] args) {

        int[][] matrix = Utils.getTestCasesMatrix_DataSet1();
        int numberOfTestCases = 8;
        int [] testcaseExecutionTime = Utils.getTestCasesExecutionTIme_DataSet1();

        IObjectiveFunction algorithmFitnessEvaluation = new APFDObjectiveFunction(matrix,numberOfTestCases);
        List<IntRange> boundConstraints = new ArrayList<>();
        for (int i = 0; i < numberOfTestCases; i++) {
            boundConstraints.add(new IntRange(1,numberOfTestCases));
        }
        IntStream.rangeClosed(1, 3)
                .forEach(i -> {
                    SalpSwarmAlgorithm salp = new SalpSwarmAlgorithm(numberOfTestCases);
                    IEvoIterationListener iEvoIterationListener = new IEvoIterationListener() {
                        @Override
                        public void onIteration(int iteration, double error) {
                           // System.out.println("iteration : "+iteration + " --- error:"+error);
                        }
                    };
                    salp.setOnIteratorListener(iEvoIterationListener);
                    salp.Compute(  new FitnessBasedOnTime(testcaseExecutionTime,false),boundConstraints);
                    System.out.println("*******************");
                    System.out.println("Attempt #" + i);
                    int[] testCaseOrder = salp.getBest();
                    double efftiveness = algorithmFitnessEvaluation.Compute(testCaseOrder);
                    System.out.println("Best test suite order: " + Arrays.toString(testCaseOrder));
                    System.out.println("efftiveness: " + efftiveness);
                    System.out.println("*******************");
                });
    }


}


