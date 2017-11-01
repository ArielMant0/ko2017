package knapsack;

/**
 * An interface for knapsack problem solvers
 *
 * @author Stephan Beyer
 */
public interface SolverInterface<SolutionType> {
	/**
	 * Compute a solution for the given instance
	 *
	 * @param instance The given knapsack instance
	 * @return The solution
	 */
	SolutionType solve(Instance instance);
}
