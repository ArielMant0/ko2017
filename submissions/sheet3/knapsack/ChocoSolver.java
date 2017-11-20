package knapsack;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.objective.ObjectiveFactory;

/**
 * CSP-Solver for binary knapsack problems
 *
 * @author Franziska Becker
 */
public class ChocoSolver implements SolverInterface<Solution> {

    /**
     * Compute a solution for the given instance
     *
     * @param instance The given knapsack instance
     * @return The solution
     */
    public Solution solve(Instance instance) {
        // Uncomment this for a solution that uses "knapsack(...)"
        return withKnapsack(instance);

        // Uncomment this for a solution that does NOT use "knapsack(...)"
        // return withoutKnapsack(instance);

        // Uncomment this for a solution that ONLY uses "solve(...)"
        // return withSolve(instance);
    }

    /**
     * Compute a solution for the given instance by
     * using the "knapsack(...)" function given by CHOCO
     *
     * @param instance The given knapsack instance
     * @return The solution
     */
    private Solution withKnapsack(Instance instance) {
        Solution optimum = new Solution(instance);
        int size = instance.getSize();

        final Model model = new Model("With Knapsack");

        final IntVar[] occurences = model.intVarArray(size, 0, 1, true);
        final IntVar weightSum = model.intVar(1, instance.getCapacity());
        final IntVar valueSum = model.intVar(1, IntVar.MAX_INT_BOUND); 

        model.knapsack(occurences, weightSum, valueSum, instance.getWeightArray(), instance.getValueArray()).post();
        final org.chocosolver.solver.Solution solution = model.getSolver().findOptimalSolution(valueSum, true);
        
        if (solution != null) {
            for (int i = 0; i < size; i++) {
                optimum.set(i, solution.getIntVal(occurences[i]));
            }
        } else {
            Logger.println("Could not find a feasible solution!");
        }

        return optimum;
    }

    /**
     * Compute a solution for the given instance without
     * using the "knapsack(...)" function given by CHOCO
     *
     * @param instance The given knapsack instance
     * @return The solution
     */
    private Solution withoutKnapsack(Instance instance) {
        Solution optimum = new Solution(instance);
        int size = instance.getSize();

        final Model model = new Model("Without Knapsack");
        final IntVar[] occurences = model.intVarArray(size, 0, 1, true);
        final IntVar weightSum = model.intVar(1, instance.getCapacity());
        final IntVar valueSum = model.intVar(1, IntVar.MAX_INT_BOUND); 
        
        // Add constraint that E w_i*x_i = weightSum 
        model.scalar(occurences, instance.getWeightArray(), "=", weightSum).post();
        // Add constraint that E c_i*x_i = valueSum 
        model.scalar(occurences, instance.getValueArray(), "=", valueSum).post();

        final org.chocosolver.solver.Solution solution = model.getSolver().findOptimalSolution(valueSum, true);
        
        if (solution != null) {
            for (int i = 0; i < size; i++) {
                optimum.set(i, solution.getIntVal(occurences[i]));
            }
        } else {
            Logger.println("Could not find a feasible solution!");
        }
        return optimum;
    }

    /**
     * Compute a solution for the given instance by
     * using only the "solve(...)" function given by CHOCO
     *
     * @param instance The given knapsack instance
     * @return The solution
     */
    private Solution withSolve(Instance instance) {
        Solution optimum = new Solution(instance);
        int size = instance.getSize();

        final Model model = new Model("With Solve");
        final IntVar[] occurences = model.intVarArray(size, 0, 1, true);
        final IntVar weightSum = model.intVar(1, instance.getCapacity());
        final IntVar valueSum = model.intVar(1, IntVar.MAX_INT_BOUND); 
        
        // Add constraint that E w_i*x_i = weightSum 
        model.scalar(occurences, instance.getWeightArray(), "=", weightSum).post();
        // Add constraint that E c_i*x_i = valueSum 
        model.scalar(occurences, instance.getValueArray(), "=", valueSum).post();

        // Make a new solution from the constructed model
        org.chocosolver.solver.Solution solution = new org.chocosolver.solver.Solution(model);
        Solver solver = model.getSolver();

        // Alternative Solutiuon: Create new objective manager that tries to maximze valueSum
        solver.setObjectiveManager(ObjectiveFactory.makeObjectiveManager(valueSum, ResolutionPolicy.MAXIMIZE));
        
        // As long as we find a better solution keep looking
        while(solver.solve()) {
            // Record the best solution
            solution.record();
        }

        if (solution != null) {
            for (int i = 0; i < size; i++) {
                optimum.set(i, solution.getIntVal(occurences[i]));
            }
        } else {
            Logger.println("Could not find a feasible solution!");
        }
        return optimum;
    }
}
