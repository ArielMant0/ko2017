package knapsack;

/**
 * Solution of a fractional knapsack problem
 *
 * @author Stephan Beyer
 */
public class FractionalSolution extends GenericSolution<Double> {
	private double epsilon = 1e-6;

	public FractionalSolution(Instance instance) {
		super(instance);
	}

	/**
	 * Copy a solution (copy constructor)
	 */
	public FractionalSolution(FractionalSolution solution) {
		super(solution);
	}

	@Override
	public void set(int item, Double quantity) {
		assert sol.size() > item : "Item number " + item + " not found!";
		assert sol.get(item) != null : "Item " + item + " not initialized in solution.";
		// Make sure we never take an item more than once
		assert quantity <= 1.0 : "Quantity exceeds item limit of 1";

		double newQuant = quantity - sol.get(item);
		solValue += newQuant * instance.getValue(item);
		solWeight += newQuant * instance.getWeight(item);
		Logger.println("weight " + solWeight);
		sol.set(item, quantity);
	}

	@Override
	public boolean isFeasible() {
		return solWeight <= instance.getCapacity();
	}

	@Override
	public boolean isBinary() {
		for (double quantity : sol) {
			if (quantity > epsilon
			 && quantity < 1 - epsilon) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected Double zero() {
		return 0.0;
	}
}
