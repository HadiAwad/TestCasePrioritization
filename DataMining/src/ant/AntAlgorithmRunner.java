package ant;

import utils.Utils;

public class AntAlgorithmRunner {

    public static void main(String[] args) {

        //Data from data set 1
        int[][] testCaseMatrix = Utils.getTestCasesMatrix_DataSet1();
        int numberOfTestCases = 8;
        int [] testcaseExecutionTime = Utils.getTestCasesExecutionTIme_DataSet1();

        AntColonyOptimization aco  = new AntColonyOptimization(numberOfTestCases , testCaseMatrix,testcaseExecutionTime);
        aco.startAntOptimization();
    }

}
