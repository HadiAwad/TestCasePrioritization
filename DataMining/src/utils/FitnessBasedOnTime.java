package utils;

public class FitnessBasedOnTime implements IObjectiveFunction{

    private int [] testCaseET;
    private boolean testCaseNumberIsZeroIndexed;

    public FitnessBasedOnTime(int [] testCaseET, boolean testCaseNumberIsZeroIndexed) {
        this.testCaseET = testCaseET;
        this.testCaseNumberIsZeroIndexed = testCaseNumberIsZeroIndexed;
    }

    @Override
    public double Compute(int[] testCaseOrder) {
        double totalTime = 0.0;

        for (int i = 0; i < testCaseOrder.length ; i++) {
            if(testCaseNumberIsZeroIndexed){
                totalTime +=this.testCaseET[testCaseOrder[i]];
            }else{
                totalTime +=this.testCaseET[testCaseOrder[i]-1];
            }

        }

        return 1/totalTime;
    }
}