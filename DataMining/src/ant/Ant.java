package ant;

import utils.APFDObjectiveFunction;
import utils.IObjectiveFunction;

public class Ant {

    protected int test_suite[];
    protected boolean usedTestSuiteBooleanArray[];
    protected IObjectiveFunction fitnessFunction;

    public Ant(IObjectiveFunction fitnessFunction, int numberOfTestCases) {
        this.test_suite = new int[numberOfTestCases];
        this.usedTestSuiteBooleanArray = new boolean[numberOfTestCases];
        this.fitnessFunction = fitnessFunction;
    }

    protected void addTestCases(int currentIndex, int testCaseidx) {
        test_suite[currentIndex + 1] = testCaseidx;
        usedTestSuiteBooleanArray[testCaseidx] = true;
    }

    protected boolean isTestCaseAdded(int i) {
        return usedTestSuiteBooleanArray[i];
    }

    public double getFitnessOfAnt() {
        return this.fitnessFunction.Compute(test_suite);
    }

    protected void clear() {
        for (int i = 0; i < usedTestSuiteBooleanArray.length; i++) {
            usedTestSuiteBooleanArray[i] = false;
            test_suite[i] = 0;
        }
    }

}