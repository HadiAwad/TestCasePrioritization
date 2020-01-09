package salp;

import Catalano.Core.IntRange;
import utils.IObjectiveFunction;

import java.util.*;

/**
 * Represents individual in the population.
 * @author Diego Catalano
 */
public class Individual implements Comparable<Individual>, Cloneable {

    private int[] location;

    private double fitness;

    public static int generateRandom(int min, int max){
        return new Random().nextInt(max-min+1) +min;
    }

    public static int[] UniformRandom(List<IntRange> ranges){
        Random rand = new Random();
        int[] r = new int[ranges.size()];
        Set<Integer> uniqueNumbers = new HashSet<>();
        for (int i = 0; i < r.length; i++) {
            IntRange range = ranges.get(i);
            int generatedNumber = -1;
            do{
                generatedNumber = generateRandom(range.getMin(),range.getMax());
            }while (uniqueNumbers.contains(generatedNumber));

            r[i] = generatedNumber;
            uniqueNumbers.add(generatedNumber);
        }
        return r;
    }

    public static List<Individual> CreatePopulation(int populationSize, List<IntRange> boundConstraints, IObjectiveFunction function){

        List<Individual> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            int[] location = UniformRandom(boundConstraints);
            double fitness = function.Compute(location);
            population.add(new Individual(location, fitness));
        }

        return population;

    }

    /**
     * Get location in the space.
     * @return Location.
     */
    public int[] getLocation() {
        return location;
    }

    /**
     * Get location in the space.
     * @param index Index.
     * @return Value.
     */
    public double getLocation(int index){
        return location[index];
    }

    /**
     * Set location in the space.
     * @param location Location.
     */
    public void setLocation(int[] location) {
        this.location = location;
    }

    /**
     * Set location in the space.
     * @param index Index.
     * @param location Location.
     */
    public void setLocation(int index, int location){
        this.location[index] = location;
    }

    /**
     * Get fitness.
     * @return Fitness.
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Set fitness.
     * @param fitness Fitness.
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Initialize a new instance of the Individual class.
     * @param location Location.
     */
    public Individual(int[] location){
        this(location, Double.NaN);
    }

    /**
     * Initialize a new instance of the Individual class.
     * @param location Location.
     * @param fitness Fitness.
     */
    public Individual(int[] location, double fitness) {
        this.location = location;
        this.fitness = fitness;
    }

    @Override
    public int compareTo(Individual o) {
        return Double.compare(fitness, o.getFitness());
    }

    public Individual getClone(){
        return new Individual(Arrays.copyOf(location, location.length), fitness);
    }

}