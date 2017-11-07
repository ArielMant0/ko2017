package knapsack;

import java.util.ArrayList;
import java.util.Comparator;
import javafx.util.Pair;

/**
 * Branch-and-Bound solver for fractional knapsack problems
 *
 * @author Franziska Becker
 */
public class BinaryHeuristicSolver implements SolverInterface<Solution> {

    private ArrayList<Pair<Integer, Integer>> order;

    /**
     * Compute a solution for the given instance
     *
     * @param instance The given knapsack instance
     * @return The solution
     */
    public Solution solve(Instance instance) {
        sortItems(instance);

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
        order = new ArrayList<Pair<Integer, Integer>>();

        for (int i = 0; i < instance.getSize(); i++) {
            order.add(i, new Pair<Integer, Integer>(i, instance.getValue(i)/instance.getWeight(i)));
        }

        order.sort(new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
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