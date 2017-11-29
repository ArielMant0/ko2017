package simulatedAnnealing;

import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;

public class SimulatedAnnealing implements SolverInterface<Solution> {

    // Current solution
    private Solution currentSol;

    // Current best solution
    private Solution bestSol;

    // Random number generator
    private Random generator;

    ///////////////////////////////
    //  Algorithm Configuration  //
    ///////////////////////////////

    // Max number of iterations and max milliseconds 
    private static final int MAX_ITERATION = 500000;
    private static final int MAX_TIME = 2000;
    
    // Which cooling function and which initial solution to use
    private static final byte DENOM = 0;
    private static final byte INITIAL = 0;

    private static final int CONSTANT = MAX_ITERATION / 2;
    
    // Whether to use time as a stop criterion or to look
    // at iteration count and last improvement
    private static final boolean USE_TIME = false;

    /**
     * Constructor, that initializes a new random generator
     */
    SimulatedAnnealing() {
        generator = new Random(System.currentTimeMillis());
    }

    /**
     * Solves a binary knapsack instance using simulated annealing
     *
     * @param instance  Problem instance
     */
    public Solution solve(Instance instance) {
        printSettings();

        generateInitialSolution(instance);

        int lastImprovement = 0, size = instance.getSize();
        long start = System.currentTimeMillis();

        for (int index = 0; !stop(index, lastImprovement, start); index++) {
            Solution next = getNeighbourSolution(size);
            if (generator.nextDouble() < Math.min(1.0, bound(index, next))) {
                currentSol = next;
                if (currentSol.getValue() > bestSol.getValue()) {
                    bestSol = new Solution(currentSol);
                    lastImprovement = 0;
                } else {
                    lastImprovement++;
                }
            }
        }

        return bestSol;
    }

    /**
     * Tests whether to stop the algorithm
     *
     * @param iter              current iteration count
     * @param lastImprovement   iteration count from the last improvement
     * @param iter              time the algorithm started
     *
     * @return wether to stop
     */
    private boolean stop(int iter, int lastImprovement, long start) {
        if (USE_TIME)
            return System.currentTimeMillis() - start >= MAX_TIME;
        else
            return iter >= MAX_ITERATION || lastImprovement >= MAX_ITERATION / 5;
    }

    /**
     * Returns a new solution from the neighbourhood of the current solution
     * by randomly taking or leaving one item
     *
     * @param size              instance size
     *
     * @return new solution
     */
    private Solution getNeighbourSolution(int size) {
        Solution solution = new Solution(currentSol);
        // Choose a random item to take/leave
        int index = generator.nextInt(size);
        solution.set(index, Math.abs(currentSol.get(index) - 1));

        return solution;
    }

    /**
     * Generates an initial solution according to the configuration
     *
     * @param instance problem instance
     */
    private void generateInitialSolution(Instance instance) {
        currentSol = new Solution(instance);
        switch (INITIAL) {
            case 0: mostValuePerWeightFirst(instance);
            case 1: leastWeightFirst(instance);
            default: randomConfiguration(instance);
        }
        // Set current best solution to initial solution
        bestSol = new Solution(currentSol);
    }

    /**
     * Sorts items of the instance by cost per weight
     *
     * @param instance  problem instance
     */
    private void mostValuePerWeightFirst(Instance instance) {
        ArrayList<Pair> order = new ArrayList<Pair>();

        for (int i = 0; i < instance.getSize(); i++) {
            order.add(i, new Pair(i, instance.getValue(i), instance.getWeight(i)));
        }

        // Sort items according to c_i/w_i >= c_i+1/w_i+1
        order.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o1.compareTo(o2) * -1;
            }
        });

        setSolution(order);
    }

    /**
     * Sorts items of the instance by weight (ascending)
     *
     * @param instance  problem instance
     */
    private void leastWeightFirst(Instance instance) {
        ArrayList<Pair> order = new ArrayList<Pair>();

        for (int i = 0; i < instance.getSize(); i++) {
            order.add(i, new Pair(i, instance.getValue(i), instance.getWeight(i)));
        }

        // Sort items according to w_i <= w_i+1
        order.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o1.w - o2.w;
            }
        });

        setSolution(order);
    }

    /**
     * Construct a random solution for instance
     *
     * @param instance  problem instance
     */
    private void randomConfiguration(Instance instance) {
        for (int i = 0; i < instance.getSize(); i++) {
            currentSol.set(i, generator.nextInt(2));
        }
    }

    /**
     * Sets a solution according to order
     *
     * @param order  List of sorted (index, value) tuples
     */
    private void setSolution(ArrayList<Pair> order) {
        for (int i = 0; i < order.size(); i++) {
            currentSol.set(order.get(i).index, 1);
        }
    }

    /**
     * Computes the bound for taking an item
     *
     * @param iter      current iteration count
     * @param next      solution to test
     *
     * @return computed value
     */
    private double bound(int iter, Solution next) {
        return Math.exp((-1.0 * (next.getValue() - bestSol.getValue())) / denominator(iter));
    }

    /**
     * Computes the denominator for the bound (annealing)
     *
     * @param iter      current iteration count
     *
     * @return computed value
     */
    private double denominator(int iter) {
        switch (DENOM) {
            case 0: return Math.pow((double) iter, 2.0);
            default: return (double) CONSTANT / Math.log(1+iter);
        }
    }

    /**
     * Prints the algorithm settings
     */
    public void printSettings() {
        Logger.println("\n---------- Simulated Annealing Settings ----------");
        Logger.println("  Max Iteration = " + MAX_ITERATION);
        Logger.println("  Denominator = " + denominatorString());
        Logger.println("  Initial Solution = " + initialSolutionString());
        Logger.println("--------------------------------------------------\n");
    }

    /**
     * Constructs a string for the denominator used for the algorithm
     *
     * @param start     value for t_0
     *
     * @return representative string
     */
    public String denominatorString() {
        switch (DENOM) {
            case 0: return "i^2";
            default: return CONSTANT + " / log(1 + i)";
        }
    }

    /**
     * Constructs a string for the initial solution strategy used for the algorithm
     *
     * @return representative string
     */
    public String initialSolutionString() {
        switch (INITIAL) {
            case 0: return "Take X most cost efficient items";
            case 1: return "Take X lightest items";
            default: return "Take X random items";
        }
    }

    /**
     * Private class that holds an index and the corresponding item.
     */
    private class Pair implements Comparable<Pair> {
        // weight, value, index
        public int w, c, index;

        public Pair(int i, int value, int weight) {
            c = value;
            w = weight;
            index = i;
        }

        public int compareTo(Pair other) {
            return (int) (this.c / this.w - other.c / other.w);
        }
    }
}