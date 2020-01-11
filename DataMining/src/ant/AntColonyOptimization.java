package ant;

import Catalano.Core.IntRange;
import utils.APFDObjectiveFunction;
import utils.FitnessBasedOnTime;
import utils.IObjectiveFunction;
import java.util.*;
import java.util.stream.IntStream;

public class AntColonyOptimization {

    private double c = 0.0; // initial phermone values
    private double evaporation = 0.1; // how much the pheromone is evaporating in every iteration
    private double antFactor = 1; // antFactor tells us how many ants we'll use per test-case
    private double randomFactor = 0.01;

    private int maxIterations = 100;

    private int numberOfTestCases;
    private int numberOfAnts;

    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();
    private double probabilities[];

    private double edgeMatrixPhermoneValues[][];

    // an array of test cases is a test suite
    private int[] bestTestCaseOrder;
    private double bestTestSuiteFitness;
    //ANT with best solution in the current status
    private Ant bestAnt = null;

    // used to evaluate fitness
    private int[][] testCaseMatrix;
    private IObjectiveFunction fitnessFunction;

    private APFDObjectiveFunction algorithmFitnessEvaluation ;

    private double globalBestTestSuiteFitness = Double.MIN_VALUE;
    private int[] globalBestTestCaseOrder = null;

    public AntColonyOptimization(int numberOfTestCases, int[][] testCaseMatrix, int[] testcaseExecutionTime) {
        this.testCaseMatrix = testCaseMatrix;
        this.numberOfTestCases = numberOfTestCases; // dont mix it with number of faults

        //to hold percentage of pheromones between iterations
        edgeMatrixPhermoneValues = new double[numberOfTestCases][numberOfTestCases];

        numberOfAnts = (int) (this.numberOfTestCases * antFactor);

        probabilities = new double[this.numberOfTestCases];

        fitnessFunction = new FitnessBasedOnTime(testcaseExecutionTime,true);
        IntStream.range(0, numberOfAnts)
                .forEach(i -> ants.add(new Ant(fitnessFunction,this.numberOfTestCases)));

        algorithmFitnessEvaluation = new APFDObjectiveFunction(testCaseMatrix,8);
    }

    /**
     * Perform ant optimization
     */
    public void startAntOptimization() {
        IntStream.rangeClosed(1, 5)
                .forEach(i -> {
                    System.out.println("*******************");
                    System.out.println("Attempt #" + i);
                    int[] testCaseOrder = solve();
                    double efftiveness = algorithmFitnessEvaluation.Compute(testCaseOrder);
                    System.out.println("Best test suite order: " + Arrays.toString(testCaseOrder));
                    System.out.println("efftiveness: " + efftiveness);
                    System.out.println("*******************");
                });
    }

    /**
     * Use this method to run the main logic
     */
    public int[] solve() {
        clearPheromone();
        IntStream.range(0, maxIterations)
                .forEach(i -> {
                    setupAndMoveAnts(i==0);
                    updateIteraionBest();
                    updatePheromoneValues();
                    updateGlobalBest();
                });
        System.out.println("Before: " + Arrays.toString(globalBestTestCaseOrder));

        int[] answer = globalBestTestCaseOrder.clone();
        HashSet<Integer> set = new HashSet<>();

        for(int i =0 ; i < answer.length ; i++ ){
            if(set.contains(answer[i])){
                answer[i] = -1;
            }else{
                set.add(answer[i]);
                answer[i] = answer[i]+1;

            }
        }
        return answer;
    }

    /**
     * Prepare ants for the simulation
     * @param b
     */
    private void setupAndMoveAnts(boolean initial) {
        int idx = 0;
        for (Ant ant : ants){
            ant.clear();
            //add a certain test case to the ant's test-cases randomly
            ant.addTestCases(-1, idx++);
        }

        int currentIndex = 0;
        if(initial){
            ants.forEach(ant -> selectNextTestcaseTillFullFaultCoverage(currentIndex, numberOfTestCases - 1,ant));
        }else {
            IntStream.range(currentIndex, numberOfTestCases - 1)
                    .forEach(i -> {
                        ants.forEach(ant -> ant.addTestCases(i, selectNextTestcase(ant.test_suite[i],ant)));
                    });
        }

    }

    private void selectNextTestcaseTillFullFaultCoverage(int startIdx, int endIDx, Ant ant) {
        IntStream.range(startIdx, endIDx )
                .forEach(i -> {
                    int next = random.nextInt(numberOfTestCases);
                    while (ant.isTestCaseAdded(next) ){
                        next = random.nextInt(numberOfTestCases);
                    }
                    ant.addTestCases(i, next);
                });
    }

    /**
     * Select next test-cases to be added to test suite
     */
    private int selectNextTestcase(int fromTestCaseNumber, Ant ant) {

        //try to add test case based on randomness
        int t = random.nextInt(numberOfTestCases );
        int nextTestCase =0;

        OptionalInt testCaseIndex = IntStream.range(0, numberOfTestCases)
                .filter(i -> i == t && !ant.isTestCaseAdded(i))
                .findFirst();
        if (testCaseIndex.isPresent()) {
            nextTestCase =  testCaseIndex.getAsInt();
        }

        if (random.nextDouble() < randomFactor) {
            return  nextTestCase;
        }

        calculateProbabilities(fromTestCaseNumber,ant);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfTestCases; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }

        return nextTestCase;
    }

    /**
     * Calculate the next TEST Case picks probabilities
     */
    public void calculateProbabilities(int fromTestCaseNumber, Ant ant) {
        for (int j = 0; j < numberOfTestCases; j++) {
            if (ant.isTestCaseAdded(j)) {
                probabilities[j] = 0.0;
            } else {
                probabilities[j] = edgeMatrixPhermoneValues[fromTestCaseNumber][j];
            }
        }
    }


    private void updatePheromoneValues() {

        //reduce Pheromone fo all testcases edges
        for (int i = 0; i < numberOfTestCases; i++) {
            for (int j = 0; j < numberOfTestCases; j++) {
                double currentValue =   edgeMatrixPhermoneValues[i][j];
                if(currentValue > 0.0){
                    currentValue*= (1-evaporation);
                    edgeMatrixPhermoneValues[i][j] *= currentValue;
                }
            }
        }


        /*
        test suite = 1,2,3

            to : 1  2   3
        from
            1   0   +1  0   // [1,2]
            2   0   0   +1    // [2,3]
            3   +1  0   0   // [3,0]
         */


        for (int i = 0; i < bestAnt.test_suite.length - 1; i++) {
            edgeMatrixPhermoneValues[bestAnt.test_suite[i]][bestAnt.test_suite[i + 1]] += 1;
        }
        edgeMatrixPhermoneValues[bestAnt.test_suite[numberOfTestCases - 1]][bestAnt.test_suite[0]] += 1;
    }

    /**
     * Update the best solution
     */
    private void updateIteraionBest() {
        if (bestTestCaseOrder == null) {
            bestTestCaseOrder = ants.get(0).test_suite;
            bestTestSuiteFitness = ants.get(0)
                    .getFitnessOfAnt();
            bestAnt= ants.get(0);
        }

        // we need to maximize the objective value
        for (Ant a : ants) {
            if (a.getFitnessOfAnt() > bestTestSuiteFitness) {
                bestTestSuiteFitness = a.getFitnessOfAnt();
                bestTestCaseOrder = a.test_suite.clone();
                bestAnt = a;
            }
        }
    }


    private void updateGlobalBest() {
        // we need to maximize the objective value
        if (bestTestSuiteFitness > globalBestTestSuiteFitness) {
            this.globalBestTestCaseOrder = bestTestCaseOrder;
            this.globalBestTestSuiteFitness = bestTestSuiteFitness;
        }
    }

    /**
     * Clear trails after simulation
     */
    private void clearPheromone() {
        IntStream.range(0, numberOfTestCases)
                .forEach(i -> {
                    IntStream.range(0, numberOfTestCases)
                            .forEach(j -> edgeMatrixPhermoneValues[i][j] = c);
                });
    }

}