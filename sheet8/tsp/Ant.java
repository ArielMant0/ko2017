package tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import java.util.Arrays;

public class Ant {

    private int end;
    private int[] nodes;
    private double value;
    private HashSet<Integer> notVisited;
   

    public Ant() {
        end = -1;
        value = 0.0;
        nodes = null;
        notVisited = null;
    }

    public Ant(int range) {
        end = -1;
        value = 0.0;
        nodes = new int[range];
        notVisited = new HashSet<Integer>(range, 1f);
        for (int i = 0; i < range; i++) {
            notVisited.add(i);
            nodes[i] = -1;
        }
    }

    public Ant(Ant other) {
        this.end = other.end;
        this.value = other.value;
        this.nodes = Arrays.copyOf(nodes, nodes.length); 
        this.notVisited = new HashSet<Integer>(other.notVisited);
    }

    public double getValue() {
        return this.value;
    }

    public int[] getPermutation() {
        return nodes;
    }

    public Set<Integer> list() {
        return this.notVisited;
    }

    public int back() {
        return this.nodes[end];
    }

    public boolean hasEdge(int from, int to) {
        for (int i = 0; i < nodes.length-1; i++) {
            if (nodes[i] == from && nodes[i+1] == to) {
                return true;
            }
        }
        return false;
    }

    public void add(int nextNode, double cost) {
        if (end < nodes.length-1) {
            nodes[++end] = nextNode;
            notVisited.remove(nextNode);
            value += cost;
        }
    }

    public void reset() {
        end = -1;
        value = 0;
        for (int i = 0; i < nodes.length; i++) {
            notVisited.add(i);
            nodes[i] = -1;
        }
    }

    public static class TspInstance {
		private double[][] distances;
        private int n;
		private double[][] tau;
		private double[][] eta;

		public TspInstance(String filename) {
			this.initializeDistances(filename);
			this.initalizeTauEta();
		}

		/**
		 * This method initializes the distances between all nodes.
		 *
		 * @param filename filename of the tsp instance
		 */
		private void initializeDistances(String filename) {
			try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
				// first line is number of nodes
				n = Integer.parseInt(reader.readLine());
				distances = new double[n][n];
    			/*
    			 * read from file
    			 * first entry is starting node,
    			 * second entry ending node,
    			 * third entry is distance
    			 */
				int start, end;
                double distance;
				String line;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split("\\s+");
					start = Integer.parseInt(tokens[0]);
					end = Integer.parseInt(tokens[1]);
					distance = Double.parseDouble(tokens[2]);

                    // dinstances are symmetric
					distances[start-1][end-1] = distance;
					distances[end-1][start-1] = distance;
				}
            } catch (IOException e) {
				System.err.println("File " + filename + " not found.");
			}
		}

		/**
		 * This method initializes the auxiliary tau and eta values
		 */
		private void initalizeTauEta() {
			tau = new double[n][n];
			eta = new double[n][n];
			
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Set initial pheromone to 1, so every way is equally likely
                    tau[i][j] = 1.0;
                    eta[i][j] = 1.0 / distances[i][j];
                }
            }
		}

        public int getSize() {
            return this.n;
        }

		public double[][] getDistances() {
			return this.distances;
		}

		public double[][] getTau() {
			return this.tau;
		}

		public void setTau(int i, int j, double value) {
            this.tau[i][j] = value;
        }

		public double[][] getEta() {
			return this.eta;
		}
	}

	public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Wrong number of arguments.");
        } else {
            TspInstance inst = new TspInstance(args[0]);

            long start, end;
            start = System.currentTimeMillis();

            int[] pi = AntSolver.solve(inst);

            System.out.println("best objective: " + getCost(inst, pi));
            System.out.println("best tour: ");
            for (int i = 0; i < pi.length; i++) {
                System.out.print(pi[i] + " ");
            }
            System.out.println();

            end = System.currentTimeMillis();

            System.out.printf("Benoetigte Zeit: %.3fs\n", (end - start) / 1000.0);
        }
	}

    public static int getCost(TspInstance instance, int[] pi) {
        int cost = 0;
        for (int i = 0; i < pi.length-1; i++) {
            cost += instance.getDistances()[i][i+1];
        }
        return cost;
    }
}
