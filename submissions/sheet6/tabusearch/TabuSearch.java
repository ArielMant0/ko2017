package tabusearch;

import java.util.Random;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Comparator;


public class TabuSearch implements SolverInterface<Solution> {

    // Best solution found
    private Solution best;
    // Tabulist
    private Vector<Attribute> tabulist;
    // Random number generator
    private Random generator;

    /////////////////////////////////////
    // Solver parameters
    /////////////////////////////////////

    // Maximum number of iterations
    private int maxIter;
    // Maximum number of iterations we allow
    // the working solution to be worse
    private int maxBad;
    // Max number of items in the tabu list
    private int maxItems;

    // Use move + objective function value in tabu list
    private boolean useMove;
    // Stop after maxIter number of iterations have been done
    private boolean stopIter;
    // Which starting solution to use
    private boolean cheapStart;
    // Only look at feasible solutions
    private boolean onlyFeasible;

    /////////////////////////////////////
    // Member functions
    /////////////////////////////////////

    public TabuSearch() {
        tabulist = new Vector<Attribute>(maxItems);
        generator = new Random(System.currentTimeMillis());
        setBoolParameters(true, true, true, true);
        maxIter = maxItems = maxBad = 0;
    }

    /**
     * Sets boolean parameters for the solver
     *
     * @param useMove       Use move + objective function value in tabu list
     * @param cheapStart    Use initial solution that sorts after value per weight
     * @param stopIter      Stop after maxIter number of iterations
     * @param onlyFeasible  Only consider feasible solutions
     */
    public void setBoolParameters(boolean useMove, boolean cheapStart, boolean stopIter, boolean onlyFeasible) {
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
        setMaxIterations(instance.getSize());
        
        // All configurations used for testing (in the same order)
        //setBoolParameters(true, true, true, true);
        //setBoolParameters(true, true, false, true);
        //setBoolParameters(true, true, true, false);
        //setBoolParameters(true, true, false, false);

        //setBoolParameters(false, true, true, true);
        //setBoolParameters(false, true, false, true);
        //setBoolParameters(false, true, true, false);
        //setBoolParameters(false, true, false, false);

        Solution work = generateInitialSolution(instance);

        int last = 0;
        for (int i = 0; !stop(i, last); i++) {
            // Generate a set of candidates and choose a new solution from it
            // and add the attribute to the tabu list
            setNextSolution(work);

            if (work.getValue() > best.getValue()) {
                // If the new solution is at least 1 % better than the previous
                // best solution, then reset last improvement counter
                if ((double) work.getValue() / best.getValue() > 0.01) {
                    last = 0;
                }
                best = new Solution(work);
            }
            last++;
        }

        return best;
    }

    /**
     * Sets the maximum number of iterations and related constants
     *
     * @param items     Number of items
     */
    private void setMaxIterations(int items) {
        this.maxIter = Math.min(500000, Math.max(100000, items * 100));
        this.maxBad = this.maxIter / 10;
        this.maxItems = this.maxIter / 5;
    }

    /**
     * Whether to stop the solver
     *
     * @param index     Current iteration index
     * @param last      Last time the solution improved
     *
     * @return whether to stop
     */
    private boolean stop(int index, int last) {
        return stopIter ? index >= maxIter : last >= maxBad;
    }

    /**
     * Given the solver configuration and the current
     * solution, choose a new solution from the set of
     * candidates and update the tabu list afterwards.
     *
     * @param work Current working solution
     */
    private void setNextSolution(Solution work) {
        int index = -1;
        int items = work.getInstance().getSize();

        if (onlyFeasible) {
            Instance instance = work.getInstance();
            int max = -1;
            
            // Check all items whether or not we can take them if we have not done so already
            for (int i = 0; i < items && index == -1; i++) {
                if (work.get(i) == 0 && instance.getWeight(i) + work.getWeight() <= instance.getCapacity() && !tabu(i, work)) {
                    index = i;
                }
            }

            // If we found an item, take it
            if (index != -1) {
                work.set(index, 1);
            } else {
                // If there is no item to take, leave one item randomly
                do {
                    index = generator.nextInt(items);
                } while(work.get(index) == 0);

                work.set(index, 0);
            }
        } else {
            // Allows infeasible solutions (randomly flip a bit)
            index = generator.nextInt(items);
            work.set(index, 1 - work.get(index));
        }

        // Update Tabulist
        if (useMove) {
            updateTabuList(new Move(index, work.getValue()));
        } else {
            updateTabuList(new ItemCounter(work));
        }
    }

    /**
     * Updates the Tabulist, deleting a third of its elements
     * when it has reached its item limit
     *
     * @param attribute The attribute to add
     */
    private void updateTabuList(Attribute attribute) {
        if (tabulist.size() == maxItems)
            tabulist = new Vector<Attribute>(tabulist.subList(maxItems/3 + 1, maxItems));

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
        return useMove ? tabulist.contains(new Move(index, solution.getValue())) : tabulist.contains(new ItemCounter(solution));
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