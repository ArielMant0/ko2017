package knapsack;

import java.lang.Math;

/**
 * Solver for binary knapsack problems
 *
 * @author Franziska Becker
 */
public class BinarySolver implements SolverInterface<Solution> {

    // (current) optimal solution
    private Solution optimal;
    // number of already computed solutions
    private int count;
    // number of elements
    private int max;

    /**
     * Constructs a solver for the given problem instance
     *
     * @param instance Problem instance
     */
    BinarySolver(Instance instance) {
        optimal = new Solution(instance);
        count = 1;
        max = instance.getSize();
    }

    /**
     * Computes all possible solutions for the given instance
     *
     * @param instance The given binary knapsack instance
     * @return The optimal solution
     */
    public Solution solve(Instance instance) {
        Logger.enable();
        Logger.println("All possible solutions:");

        // Call this method to use a recursion-based approach
        // to solve the problem
        //useRecursion(new Solution(instance));
        
        // Call this method to use a loop-based approach
        // to solve the problem 
        useLoop(new Solution(instance));

        Logger.println();
        Logger.disable();

        return optimal;
    }

    /**
     * Computes all possible solutions for the given
     * starting solution using recursion
     *
     * @param sol The starting solution
     */
    public void useRecursion(Solution sol) {
        recursiveSolve(0, sol);
        sol.set(0, 1);
        recursiveSolve(0, sol);
    }

    /**
     * Computes all possible solutions for the given
     * starting solution using a loop
     *
     * @param sol The starting solution
     */
    public void useLoop(Solution sol) {
        loopSolve(sol, (int) Math.pow(2.0, max));
    }

    private void loopSolve(Solution sol, int limit) {
        // Check all 2^n (n = max) possible combinations
        for (int i = 0; i < limit; i++) {
            // For every item, compute whether it should be taken or not
            for(int j = 1; j <= max; j++) {
                int power = (int) Math.pow(2.0, max - j);
                int quantity = i & power;
                quantity = quantity >> (max - j);
                sol.set(j-1, quantity);
            }
            // Print solution
            Logger.println("Solution " + (count++) + ": ZFW = " + sol.getValue() + ", Ew = " + sol.getWeight() + ", feasible = " + sol.isFeasible());
            // Check for optimality
            if (sol.isFeasible() && sol.getValue() > optimal.getValue()) {
                optimal = new Solution(sol);
            }
        }
    }

    private void recursiveSolve(int index, Solution sol) {
        // Anker
        if (index == max - 1) {
            sol.set(index, 1);
            // Print solution
            Logger.println("Solution " + (count++) + ": ZFW = " + sol.getValue() + ", Ew = " + sol.getWeight() + ", feasible = " + sol.isFeasible());
            // Check for optimality
            if (sol.isFeasible() && sol.getValue() > optimal.getValue()) {
                optimal = new Solution(sol);
            }

            return;
        }

        // Fork in order to try out all possibilities (e.g. 000  and 010)
        recursiveSolve(index+1, new Solution(sol));
        sol.set(index+1, 1);
        recursiveSolve(index+1, new Solution(sol));
    }
}