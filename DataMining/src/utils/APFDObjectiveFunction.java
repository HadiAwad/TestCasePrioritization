package utils;

public class APFDObjectiveFunction implements IObjectiveFunction {

    private int[][] matrix;
    private int numberOfTestCases;

    public APFDObjectiveFunction( int[][] matrix, int numberOfTestCases){
        this.matrix = matrix;
        this.numberOfTestCases = numberOfTestCases;
    }

    @Override
    public double Compute(int[] order) {
        int apfd = 0;
        double summation = 0.0;
        int numberOfTestFaults = matrix.length;

        for (int i = 0; i < matrix.length; i++) {
            int index = Integer.MAX_VALUE;

            for (int j = 0; j < matrix[i].length; j++) {
                int newIndex = findIndexFromWithinOrder(order, matrix[i][j]);
                if (newIndex != -1 && newIndex < index) {
                    index = newIndex;
                }
            }
            //if it was not updated then leave it as huge value;
            summation += index;
        }

        return (1.0 - (summation / (numberOfTestCases * numberOfTestFaults))) + (1.0 / (2.0 * numberOfTestCases));
    }

    public int findIndexFromWithinOrder(int[] order, int testCaseNumber) {
        for (int i = 0; i < order.length; i++) {
            if (((int) order[i]) == testCaseNumber) {
                return i + 1;
            }
        }
        return -1;
    }
}
