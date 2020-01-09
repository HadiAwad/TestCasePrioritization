package salp;

import Catalano.Core.IntRange;
import Catalano.Math.Tools;
import utils.IObjectiveFunction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Salp Swarm Algorithm (SSA).
 *
 * The main inspiration of SSA is the swarming behaviour of salps when navigating and foraging in oceans.
 *
 * References:
 * S. Mirjalili, A.H. Gandomi, S.Z. Mirjalili, S. Saremi, H. Faris, S.M. Mirjalili.
 * "Salp Swarm Algorithm: A bio-inspired optimizer for engineering design problems." Advances in Engineering Software 114 (2017): 163-191.
 *
 * @author Diego Catalano
 */
public class SalpSwarmAlgorithm extends BaseEvolutionaryOptimization {


    private boolean maximize = true;

    public SalpSwarmAlgorithm() {
        this(30,100);
    }

    public SalpSwarmAlgorithm(int population) {
        this(population,100);
    }

    public SalpSwarmAlgorithm(int population, int generations){
        this.populationSize = population;
        this.generations = generations;
    }



    @Override
    public void Compute(IObjectiveFunction function, List<IntRange> boundConstraints) {

        Random rand = new Random();

        //Reset variables
        minError = Double.MAX_VALUE;
        nEvals = 0;

        //Create the population
        List<Individual> population = Individual.CreatePopulation(populationSize, boundConstraints, function);
        Collections.sort(population);

        int index = 0;
        if(maximize){
            index =   population.size()-1;
        }

        minError = population.get(index).getFitness();
        best = Arrays.copyOf(population.get(index).getLocation(), boundConstraints.size());

        Collections.shuffle(population);

        //Main algorithm
        for (int g = 0; g < generations; g++) {

            double c1 = 2 * Math.exp(-Math.pow(4*(g+1)/generations,2));

            for (int i = 0; i < population.size(); i++) {

                if(i <= population.size() / 2){
                    for (int j = 0; j < boundConstraints.size(); j++) {
                        IntRange range = boundConstraints.get(j);
                        if(rand.nextDouble() < 0.5)
                            population.get(i).getLocation()[j] = best[j] + (int)Math.ceil(c1 * Individual.generateRandom(range.getMin(), range.getMax()));
                        else
                            population.get(i).getLocation()[j] = best[j] - (int)Math.ceil(c1 * Individual.generateRandom(range.getMin(), range.getMax()));
                    }
                }

                else if (i > (population.size()/2) && i < population.size()){
                    int[] salp1 = population.get(i-1).getLocation();
                    int[] salp2 = population.get(i).getLocation();

                    for (int j = 0; j < salp2.length; j++)
                        salp2[j] = (salp2[j] + salp1[j]) / 2;

                }
            }

            //Clamp values
            for (int i = 0; i < population.size(); i++)
                Tools.Clamp(population.get(i).getLocation(), boundConstraints);


            //Calculate fitness
            for (int i = 0; i < population.size(); i++) {
                double f = function.Compute(population.get(i).getLocation());
                population.get(i).setFitness(f);
                if(f<0){
                    System.out.println("fitness is less than 0 "+f);
                }
                nEvals++;
                if(f > minError){
                    minError = f;
                    best = Arrays.copyOf(population.get(i).getLocation(), boundConstraints.size());
                }
            }

            //Update on listener
            if(listener != null) listener.onIteration(g+1, minError);

        }
    }
}
