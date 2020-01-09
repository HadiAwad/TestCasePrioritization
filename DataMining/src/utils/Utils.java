package utils;

import java.util.stream.IntStream;
import java.util.Random;

public class Utils {

    public static int[][] getTestCasesMatrix(){
        int testCasenumber = 3;
        int faultsNUmber = 4;
        int [][] matrix= new int[faultsNUmber][testCasenumber];
        matrix[0] = new int[]{3};
        matrix[1] = new int[]{2};
        matrix[2] = new int[]{1,3};
        matrix[3] = new int[]{3};

        return matrix;
    }


    public static int[][] getTestCasesMatrix_DataSet1(){
        int testCasenumber = 8;
        int faultsNumber = 10;
        int [][] matrix= new int[faultsNumber][testCasenumber];
        matrix[0] = new int[]{2,3,6};
        matrix[1] = new int[]{1,4,8};
        matrix[2] = new int[]{2,5,7};
        matrix[3] = new int[]{1,4};
        matrix[4] = new int[]{3};
        matrix[5] = new int[]{5,7};
        matrix[6] = new int[]{1,3,6};
        matrix[7] = new int[]{3,7};
        matrix[8] = new int[]{1,4};
        matrix[9] = new int[]{5,8};

        return matrix;
    }

    public static int[] getTestCasesExecutionTIme_DataSet1(){
        int [] et= new int[]{7,4,5,4,4,5,4,5};
        return et;
    }

    public static int factorial(int n){
        if (n == 0)
            return 1;
        else
            return(n * factorial(n-1));
    }

    /**
     * Generate initial solution
     */
    public double[][] generateRandomMatrix(int n) {
        double[][] randomMatrix = new double[n][n];
        IntStream.range(0, n)
                .forEach(i -> IntStream.range(0, n)
                        .forEach(j -> randomMatrix[i][j] = Math.abs(new Random().nextInt(100) + 1)));
        return randomMatrix;
    }
}
