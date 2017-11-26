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

    private int lastIter;

    private final int MAX_ITERATION = 500000;
    private final double ALPHA = 0.9;
    private final byte DENOM = 1;
    private final byte INITIAL = 1;

    public Solution solve(Instance instance) {
        lastIter = MAX_ITERATION / 2;
        printSettings(lastIter);

        initMembers(instance);

        generateInitialSolution(instance);

        int lastImprovement = 0;
        int threshold = MAX_ITERATION / 5;

        for (int index = 0; index < MAX_ITERATION && lastImprovement < threshold; index++) {
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
        System.out.println("\n---------- Simulated Annealing Settings ----------");
        System.out.println("  Max Iteration = " + MAX_ITERATION);
        System.out.println("  Denominator = " + denominatorString(start));
        System.out.println("  Initial Solution = " + initialSolutionString());
        System.out.println("--------------------------------------------------\n");
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