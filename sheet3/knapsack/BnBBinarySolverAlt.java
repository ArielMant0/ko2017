package knapsack;

import java.util.ArrayList;
import java.util.Comparator;

import javafx.util.Pair;

/**
 * Branch-and-Bound solver for Binary knapsack problems
 *
 * @author Tobias Loske
 */
public class BnBBinarySolverAlt implements SolverInterface<Solution> {

    private ArrayList<Pair<Integer, Double>> order;
    private int optimumValue;

	/**
	 * Solves the given instance of the knapsack Problem
	 * @param instance the instance of the knapsack problem
	 * @return the optimal Solution to the Problem
	 */
    public Solution solve(Instance instance) {
        sortItems(instance);

        Solution optimum = getStartingSolution(instance);
        optimumValue = optimum.getValue();

        optimum = recSolve(instance, new Solution(instance), 0);

        return optimum;
    }

    /**
     * Determines the optimal Solution to the problem using recursion
     * @param instance	the given knapsack Problem
     * @param solution  A Solution to the Problem
     * @param index which item is going to be put into the knapsack
     * @return  the optimal Solution
     */
    private Solution recSolve(Instance instance, Solution solution, int index) {
        if (index == order.size() - 1) {
            if (solution.getValue() > optimumValue) {
                optimumValue = solution.getValue();
            }
            return solution;
        } else if (calculateUpperBound(instance, solution, index - 1) < optimumValue) {
            return solution;
        }

        Solution sol1 = new Solution(solution);

        Solution sol2 = new Solution(solution);
        sol2.set(order.get(index).getKey(), 1);

        if (sol2.isFeasible()) {
            if (calculateUpperBound(instance, sol1, index) > calculateUpperBound(instance, sol2, index)) {
                sol1 = recSolve(instance, sol1, index + 1);
                sol2 = recSolve(instance, sol2, index + 1);
            } else {
                sol2 = recSolve(instance, sol2, index + 1);
                sol1 = recSolve(instance, sol1, index + 1);
            }

            if (sol1.getValue() > sol2.getValue()) {
                return sol1;
            } else {
                return sol2;
            }
        } else {
            sol1 = recSolve(instance, new Solution(sol1), index + 1);
            return sol1;
        }
    }

    /**
     * Calculates the UpperBound of a given Solution
     * @param instance the knapsack instance
     * @param solution the solution for which the upper bound shall be calculated
     * @param index which item is going to be put into the knapsack
     * @return  the upperBound of the Solution
     */
    private int calculateUpperBound(Instance instance, Solution solution, int index) {
        int upperBound = (int) (solution.getValue() + (order.get(index + 1).getValue() * (instance.getCapacity() - solution.getWeight())) + 0.5);
        return upperBound;
    }

    /**
     * Compute a solution for the given instance
     *
     * @param instance The given knapsack instance
     * @return The solution
     */
    private Solution getStartingSolution(Instance instance) {

        Solution optimum = new Solution(instance);
        int number = instance.getCapacity();
        double rest = number;

        // Fill up the knapsack until it's full, starting
        // with the most cost efficient item
        for (int i = 0; i < order.size() && optimum.isFeasible(); i++) {
            int item = order.get(i).getKey();
            optimum.set(item, instance.getWeight(item) <= rest ? 1 : 0);
            rest = number - optimum.getWeight();
        }

        return optimum;
    }

    /**
     * Compute item order according to c_1/w_1 >= ... >= c_n/w_n
     *
     * @param instance The given knapsack instance
     */
    private void sortItems(Instance instance) {
        order = new ArrayList<Pair<Integer, Double>>();

        for (int i = 0; i < instance.getSize(); i++) {
            order.add(i, new Pair<Integer, Double>(i, ((double) instance.getValue(i)) / ((double) instance.getWeight(i))));
        }

        order.sort(new Comparator<Pair<Integer, Double>>() {
            @Override
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (o1.getValue() > o2.getValue())
                    return -1;
                else if (o1.getValue() < o2.getValue())
                    return 1;
                else
                    return 0;
            }
        });
    }
}