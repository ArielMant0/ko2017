package knapsack;

import java.util.ArrayList;
import java.util.Comparator;
import javafx.util.Pair;

/**
 * Branch-and-Bound solver for fractional knapsack problems
 *
 * @author Franziska Becker
 */
public class FractionalSolver implements SolverInterface<FractionalSolution> {

    private ArrayList<Pair<Integer, Double>> order;

    /**
     * Compute a solution for the given instance
     *
     * @param instance The given knapsack instance
     * @return The solution
     */
    public FractionalSolution solve(Instance instance) {
        sortItems(instance);

        FractionalSolution optimum = new FractionalSolution(instance);
        int number = instance.getCapacity();
        double rest = number;

        // Fill up the knapsack until it's full, starting
        // with the most cost efficient item
        for (int i = 0; i < order.size() && optimum.isFeasible(); i++) {
            int item = order.get(i).getKey();
            // Clamp item quantity betwen 0.0 and 1.0
            optimum.set(item, Math.min(1.0, Math.max(0.0, (double) rest/instance.getWeight(item))));
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
            order.add(i, new Pair<Integer, Double>(i, (double) instance.getValue(i)/instance.getWeight(i)));
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