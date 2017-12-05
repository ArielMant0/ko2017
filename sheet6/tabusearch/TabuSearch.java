package tabusearch;

import java.util.Random;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Comparator;


public class TabuSearch implements SolverInterface<Solution> {

    // Best solution found
    private Solution best;

    // Tabulist
    private ArrayDeque<Attribute> tabulist;

    // Random number generator
    private Random generator;

    /////////////////////////////////////
    // Solver parameters
    /////////////////////////////////////

    // Maximum number of iterations
    private int maxIter = 500000;
    // Maximum number of iterations we allow
    // the working solution to be worse
    private int maxBad = maxIter / 10;
    // Max number of items in the tabu list
    private int maxItems = 10000;

    // Use move + objective function value in tabu list
    private boolean useMove = true;
    // Stop after maxIter number of iterations have been done
    private boolean stopIter = true;
    // Which starting solution to use
    private boolean cheapStart = true;
    // Only look at feasible solutions
    private boolean onlyFeasible = true;

    /////////////////////////////////////
    // Member functions
    /////////////////////////////////////

    public TabuSearch() {
        tabulist = new ArrayDeque<Attribute>();
        generator = new Random(System.currentTimeMillis());
    }

    /**
     * Sets integer parameters for the solver
     *
     * @param maxIter       Maximum number of iterations
     * @param maxBad        Maximum number of not bettering iterations
     * @param maxItems      Max number of items in the tabu list
     */
    public void setIntParameters(int maxIter int maxBad, int maxItems) {
        this.maxIter = maxIter > 0 ? maxIter : this.maxIter;
        this.maxBad = maxBad > 0 ? maxBad : this.maxBad;
        this.maxItems = maxItems > 0 ? maxItems : this.maxItems;
    }

    /**
     * Sets boolean parameters for the solver
     *
     * @param useMove       Use move + objective function value in tabu list
     * @param stopIter      Stop after maxIter number of iterations
     * @param cheapStart    Use initial solution that sorts after value per weight
     * @param onlyFeasible  Only consider feasible solutions
     */
    public void setBoolParameters(boolean useMove, boolean stopIter, boolean cheapStart, boolean onlyFeasible) {
        this.useMove = useMove;
        this.stopIter = stopIter;
        this.cheapStart = cheapStart;
        this.onlyFeasible = onlyFeasible;
    }

    /**
     * Compute a solution for the given instance
     *
     * @param instance The given knapsack instance
     *
     * @return solution
     */
    public Solution solve(Instance instance) {
        Solution work = generateInitialSolution(instance);

        for (int i = 0; !stop(i); i++) {
            // Generate a set of candidates and choose a new solution from it
            // and add the attribute to the tabu list
            setNextSolution(work);

            if (work.getValue() > best.getValue())
                best = new Solution(work);
        }

        return best;
    }

    /**
     * Whether to stop the solver
     *
     * @param index Current iteration index
     *
     * @return whether to stop
     */
    private boolean stop(int index) {
        return index >= maxIter;
    }

    /**
     * Given the solver configuration and the current
     * solution, choose a new solution from the set of
     * candidates and update the tabu list afterwards.
     *
     * @param work Current working solution
     */
    private void setNextSolution(Solution work) {
        int index;

        if (onlyFeasible) {
            Instance instance = work.getInstance();
            int items = instance.getSize();
            boolean[] candidate = new boolean[items];
            boolean atLeastOne = false;
            
            // Check all items whether or not we can take them if we have not done so already
            for (int i = 0; i < items; i++) {
                candidate[i] = work.get(i) == 0 && instance.getWeight(i) + work.getWeight() <= instance.getCapacity() && !tabu(i, work);
                // Save that we have at least one item that has not been taken yet and fits
                if (candidate[i])
                    atLeastOne = true;
            }

            index = generator.nextInt(items);
            if (atLeastOne) {
                // Randomly take an item that still fits and was not taken before
                while (!candidate[index]) {
                    index = generator.nextInt(items);
                }
                work.set(index, 1);
            } else {
                // All items are taken (or don't fit), so randomly leave one
                work.set(index, 0);
            }

        } else {         
            // TODO: allow infeasible solutions, but make them really costly
        }

        // Update Tabu list
        if (useMove) {
            updateTabuList(new Move(index, work.getValue()));
        } else {
            // TODO: think of another way to represent a solution
        }
    }

    /**
     * Updates the Tabulist, deleting a third of its elements
     * when it has reached its item limit
     *
     * @param attribute The attribute to add
     */
    private void updateTabuList(Attribute attribute) {
        if (tabulist.size() == maxItems) {
            for (int i = 0; i < maxItems/3; i++) {
                tabulist.removeFirst();
            }
        }

        tabulist.add(attribute);
    }

    /**
     * Checks whether solutions is already in the tabu list
     *
     * @param index Current iteration index
     * @param solution Current solution
     *
     * @return whether solution is tabu
     */
    private boolean tabu(int index, Solution solution) {
        return useMove ? tabulist.contains(new Move(index, solution.getValue())) : tabulist.contains(new Move());
    }

    /**
     * Generates an initial solution
     *
     * @param instance The given knapsack instance
     *
     * @return generated solution
     */
    private Solution generateInitialSolution(Instance instance) {
        // TODO: neirest neighbour instead of randomStart?        
        Solution solution = cheapStart ? sortByCheapest(instance) : randomStart(instance);

        best = new Solution(solution);

        return solution;
    }

    /**
     * Generates an initial solution by randomly
     * choosing elements until the knapsack is full or all
     * items have been inspected.
     *
     * @param instance The given knapsack instance
     *
     * @return generated solution
     */
    private Solution randomStart(Instance instance) {
        Solution solution = new Solution(instance);

        int index;
        int size = instance.getSize();
        int capacity = instance.getCapacity();

        for (int i = 0; i < size && solution.getWeight() < capacity; i++) {
            index = generator.nextInt(size);
            if (instance.getWeight(index) + solution.getWeight() <= capacity)
                solution.set(index, 1);
        }

        return solution;
    }

    /**
     * Generates an initial solution by sorting all
     * elements by cost (c_1/w_1 >= c_2/w_2 >= .. >= c_n/w_n)
     * and then packs as many items as possible
     *
     * @param instance The given knapsack instance
     *
     * @return generated solution
     */
    private Solution sortByCheapest(Instance instance) {
        Solution solution = new Solution(instance);
        ArrayList<Pair> order = new ArrayList<Pair>();

        for (int i = 0; i < instance.getSize(); i++) {
            order.add(i, new Pair(i, (double) instance.getValue(i), (double) instance.getWeight(i)));
        }

        order.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o1.cw > o2.cw ? -1 : (o1.cw < o2.cw ? 1 : 0);
            }
        });

        int index;
        int capacity = instance.getCapacity();

        for (int i = 0; i < order.size(); i++) {
            index = order.get(i).index;
            if (solution.getWeight() + instance.getWeight(index) <= capacity)
                solution.set(index, 1);
        }

        return solution;
    }

    /**
     * Private class that holds an index and the computed
     * value-to-weight ratio of that item.
     *
     */
    private class Pair implements Comparable<Pair> {
        public double cw;
        public int index;

        public Pair(int i, double value, double weight) {
            cw = value / weight;
            index = i;
        }

        public int compareTo(Pair other) {
            return (int) (this.cw - other.cw);
        }
    }
}