package tsp;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class that solves a traveling salesman problem instance by
 * employing ant colony optimization
 */
public class AntSolver {

    // List of all ants
    private static ArrayList<Ant> ants = null;
    // TSP instance
    private static Ant.TspInstance instance = null;
    // Random number generator
    private static Random random = new Random();

    // Number of nodes of the tsp instance
    private static int elements = 0;
    // Maximum number of iterations
    private static int maxIter = 0;
    // Maximum number of not improving iterations
    private static int maxImprov = 0;
    // Maximum difference between best and worst solution to count
    private static double maxDiff = 0;

    // Delta values, [i][j][k] -> delta for edge from node i to node j for ant k
    private static double[][][] delta;
    // How important heuristic information is
    private static double alpha = 0.6f;
    // How important pheromones are
    private static double beta = 0.4f;
    // How quickly pheromones evaporate
    private static double evaporate = 0.1f;   

    // Whether to use a constant count of ants
    private static boolean constant = true;
    // Whether to stop when reaching the maximum number of iterations
    private static boolean useIter = true;

    /**
     * Set algorithm hyperparameters (just two options)
     *
     * @param byte          determines which configuration to set
     */
    public static void config(byte which) {
        switch (which) {
            default:
            case 0: constant = true; useIter = true; break;
            case 1: constant = false; useIter = true; break;
            case 2: constant = true; useIter = false; break;
            case 3: constant = false; useIter = false; break;
        }
    }

    /**
     * Solve the given tsp instance
     *
     * @param instance      the given instance
     *
     * @return the best order of nodes that was found
     */
    public static int[] solve(Ant.TspInstance tsp) {
        instance = tsp;
        elements = instance.getSize();
        maxIter = Math.min(elements * 100, 500000);
        maxImprov = maxIter / 20;
        maxDiff = (double) elements / 5;

        // Create ants
        createAnts();

        int start, improv = 0;
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        // Main loop
        for (int i = 0; !stopIteration(i, improv); i++) {
            // Reset all ants to prepare this iteration
            resetAnts();

            // Determine the starting node for all ants
            start = random.nextInt(elements);
            for (int j = 0; j < ants.size(); j++) {
                // Set the starting node for ant j
                ants.get(j).add(start, 0);
                // Let ant j traverse the graph
                for (int k = 0; k < elements-1; k++) {
                    moveAnt(j);
                }
            }
            if (!useIter) {
                // Compute max solution value difference
                for (int j = 0; j < ants.size(); j++) {
                    if (ants.get(j).getValue() < min)
                        min = ants.get(j).getValue();
                    if (ants.get(j).getValue() > max)
                        max = ants.get(j).getValue();
                }
                if (max - min <= maxDiff)
                    improv++;
                else
                    improv = 0;
            }

            // Update all delta values
            updateDeltas();

            // Update all tau values
            updateTaus();
        }

        return getBestSolution();
    }

    /**
     * Create and initialize ant and delta list
     */
    private static void createAnts() {
        int capacity = constant ? 250 : Math.min(elements * 10, 10000);
        ants = new ArrayList<Ant>(capacity);
        delta = new double[elements][elements][capacity];

        for (int i = 0; i < capacity; i++) {
            ants.add(new Ant(elements));
        }
    }

    /**
     * Whether to stop the algorithm
     *
     * @param itrer         current iteration index
     * @param improv        how many iterations the maximum solution difference
     *                      has not been more than the given minimum difference
     *
     * @return whether to stop
     */
    private static boolean stopIteration(int iter, int improv) {
        return  useIter ? iter >= maxIter : iter >= maxIter || improv >= maxImprov;
    }

    /**
     * Reset all ants
     */
    private static void resetAnts() {
        for (int i = 0; i < ants.size(); i++) {
            ants.get(i).reset();
        }
    }

    /**
     * Chooses the next node for an ant to visit
     *
     * @param index         ant index
     */
    private static void moveAnt(int index) {
        int currentNode = ants.get(index).back();
        int nextNode = -1;

        double tmp = 0.0;
        double probability = 0.0;
        double denom = 0.0;
        // Compute denominator for probability formula
        for (Integer node : ants.get(index).list()) {
            denom += Math.pow(instance.getTau()[currentNode][node], alpha) * Math.pow(instance.getEta()[currentNode][node], beta);
        }

        // Get the max probabilty for all nodes that have not yet been visited
        for (Integer node : ants.get(index).list()) {
            tmp = getProbability(index, currentNode, node, denom);
            if (tmp > probability) {
                probability = tmp;
                nextNode = node;
            }
        }

        ants.get(index).add(nextNode, instance.getDistances()[currentNode][nextNode]);
    }

    /**
     * Computes the probability to choose a specific node to visit
     *
     * @param index         ant index
     * @param from          first node from edge {from, to}
     * @param to            second node from edge {from, to}
     * @param denom         formula denominator
     *
     * @return probability for edge {from, to} for ant at index
     */
    private static double getProbability(int index, int from, int to, double denom) {
        return (Math.pow(instance.getTau()[from][to], alpha) * Math.pow(instance.getEta()[from][to], beta)) / denom;
    }

    /**
     * Updates all delta values
     */
    private static void updateDeltas() {
        for (int i = 0; i < elements; i++) {
            for (int j = 0; j < elements; j++) {
                for (int k = 0; k < ants.size(); k++) {
                    delta[i][j][k] = ants.get(k).hasEdge(i, j) ? 1f / ants.get(k).getValue() : 0f;
                }
            }
        }
    }

    /**
     * Updates all tau values
     */
    private static void updateTaus() {
        double sum;
        for (int i = 0; i < elements; i++) {
            for (int j = 0; j < elements; j++) {
                sum = 0f;
                for (int k = 0; k < ants.size(); k++) {
                    sum += delta[i][j][k];
                }
                instance.setTau(i, j, (1f-evaporate)*instance.getTau()[i][j] + sum);
            }
        }
    }

    /**
     * Searches for and returns the best solution found by all ants
     *
     * @return the best permutation that was found
     */
    private static int[] getBestSolution() {
        int index = 0;
        double max = 0.0;
        for (int i = 0; i < ants.size(); i++) {
            if (ants.get(i).getValue() > max) {
                max = ants.get(i).getValue();
                index = i;
            }
        }
        return ants.get(index).getPermutation();
    }
}