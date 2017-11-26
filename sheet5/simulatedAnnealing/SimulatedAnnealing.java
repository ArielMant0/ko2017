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

    // Used for the recursive cooling function, saves time
    private int lastIter;

    // Max number of iterations and max milliseconds 
    private static final int MAX_ITERATION = 500000;
    private static final int MAX_TIME = 2000;

    // Alpha used for the recursive cooling function
    private static final double ALPHA = 0.9;
    
    // Which cooling function and which initial solution to use
    private static final byte DENOM = 0;
    private static final byte INITIAL = 0;
    
    // Whether to use time as a stop criterion or to look
    // at iteration count and last improvement
    private static final boolean USE_TIME = true;

    public Solution solve(Instance instance) {
        lastIter = MAX_ITERATION / 2;
        printSettings(lastIter);

        initMembers(instance);

        generateInitialSolution(instance);

        int lastImprovement = 0;
        long start = System.currentTimeMillis();

        for (int index = 0; !stop(index, lastImprovement, start); index++) {
            Solution next = getNeighbourSolution(instance);
            if (generator.nextDouble() < Math.min(1.0, bound(index, next))) {
                currentSol = next;
                if (currentSol.getValue() > bestSol.getValue()) {
                    bestSol = currentSol;
                    lastImprovement = 0;
                } else {
                    lastImprovement++;
                }
            }
        }

        return bestSol;
    }

    private boolean stop(int index, int lastImprovement, long start) {
        if (USE_TIME)
            return System.currentTimeMillis() - start >= MAX_TIME;
        else
            return index >= MAX_ITERATION || lastImprovement >= MAX_ITERATION / 5;
    }

    private Solution getNeighbourSolution(Instance instance) {
        Solution solution = new Solution(instance);
        // Choose a random item to take/leave
        int index = generator.nextInt(instance.getSize());
        // Decide whether to take item[index] or not (~ 50%)
        int newValue = generator.nextBoolean() ? 1 : -1;

        solution.set(index, newValue);

        return solution;
    }

    private void generateInitialSolution(Instance instance) {
        switch (INITIAL) {
            case 0: mostValuePerWeightFirst(instance);
            case 1: leastWeightFirst(instance);
            default: randomConfiguration(instance);
        }
        // Set current best solution to initial solution
        bestSol = currentSol;
    }

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

    private void randomConfiguration(Instance instance) {
        for (int i = 0; i < instance.getSize(); i++) {
            currentSol.set(i, generator.nextInt(2));
        }
    }

    private void setSolution(ArrayList<Pair> order) {
        for (int i = 0; i < order.size(); i++) {
            currentSol.set(order.get(i).index, 1);
        }
    }

    private double bound(int iter, Solution next) {
        return Math.exp((-1.0 * (next.getValue() - bestSol.getValue())) / denominator(iter));
    }

    private double denominator(int iter) {
        switch (DENOM) {
            case 0: return Math.pow((double) iter, 2.0);
            default: return lastIter *= ALPHA;
        }
    }

    private void initMembers(Instance instance) {
        bestSol = new Solution(instance);
        currentSol = new Solution(instance);
        generator = new Random(System.currentTimeMillis());
    }

    public void printSettings(int start) {
        Logger.println("\n---------- Simulated Annealing Settings ----------");
        Logger.println("  Max Iteration = " + MAX_ITERATION);
        Logger.println("  Denominator = " + denominatorString(start));
        Logger.println("  Initial Solution = " + initialSolutionString());
        Logger.println("--------------------------------------------------\n");
    }

    public String denominatorString(int start) {
        switch (DENOM) {
            case 0: return "i^2";
            default: return "t_0 = " + start + ", t_i = t_i-1 * " + ALPHA;
        }
    }

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
        public int w, c, index;

        public Pair(int i, int value, int weight) {
            c = value;
            w = weight;
            index = i;
        }

        public int compareTo(Pair other) {
            return (int) ((double) this.c / this.w - (double) other.c / other.w);
        }
    }
}