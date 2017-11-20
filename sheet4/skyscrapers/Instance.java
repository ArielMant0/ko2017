package skyscrapers;

/**
 * A skyscrapers problem instance containing...
 *  ... a matrix that represents the game field with all sub fields. If the value of a subfield is fixed, a value > 0 is
 *  given, zero indicates that the value of the field is not fixed.
 *  ... four arrays with north /south / east / west constraints that indicate the number of visible skyscrapers in
 *  this cardinal direction.
 *
 * @author Sven Boge
 */
public class Instance {
	/**
	 * Game field matrix
	 * The first index runs in north south direction,
	 * the second index runs in west east direction.
	 */
	private int[][] gamefield;

	/**
	 * Solution to the given instance
	 */
	private int[][] solution;

	/**
	 * Arrays refering to the four cardinal directions
	 */
	private int[] north;
	private int[] east;
	private int[] south;
	private int[] west;

	/**
	 * Constructs an instance for given matrix and arrays.
	 *
	 * @param matrix game field matrix
	 * @param north north constraints
	 * @param east east constraints
	 * @param south south constraints
	 * @param west west constraints
	 */
	public Instance(int[][] matrix, int[] north, int[] east, int[] south, int[] west) {
        if(north.length != east.length || east.length != west.length || west.length != south.length
				|| south.length != matrix.length || matrix.length != matrix[0].length) {
			throw new IllegalArgumentException("Constraint arrays do not fit game field.");
		}
        this.gamefield = matrix;
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
		this.solution = null;
	}

	/**
	 * Get the size of the gamefield.
	 */
	public int getGamefieldSize() {
		return this.gamefield.length;
	}

	public int[][] getGamefield() {
		return gamefield;
	}

	public int[] getNorth() {
		return north;
	}

	public int[] getEast() {
		return east;
	}

	public int[] getSouth() {
		return south;
	}

	public int[] getWest() {
		return west;
	}

	public int[][] getSolution() {
		return solution;
	}

	/**
	 * Prints the solution if available.
	 */
	public void printSolution() {
		if(this.solution == null) {
			System.out.println("Board output omitted, no solution found.");
		} else {
			printMatrix(this.solution);
		}
	}

	/**
	 * Prints the gamefield.
	 */
	public void printGamefield() {
		printMatrix(this.gamefield);
	}

	/**
	 * Prints a given matrix and adds north, west, east, south constraints.
	 *
	 * @param matrix the matrix ti print
	 */
	private void printMatrix(int[][] matrix) {
        final String lineSep = "\n";
		if(this.getGamefieldSize() > 9) {
			System.out.println("Board output omitted due to size.");
		} else {
			StringBuilder ret = new StringBuilder();
			// append north
			ret.append("  ");
			for (int j = 0; j < this.getGamefieldSize(); j++) {
				ret.append("  ");
				if(this.north[j] != 0) {
					ret.append(this.north[j]);
				} else {
					ret.append(" ");
				}
				ret.append(" ");
			}
			ret.append(lineSep);
			String rowSep = "  +";
			for (int i = 0; i < this.getGamefieldSize(); i++)
				rowSep += "---+";
			rowSep += lineSep;
			ret.append(rowSep);
			for (int j = 0; j < this.getGamefieldSize(); j++) {
				if(this.west[j] != 0) {
					ret.append(this.west[j] + " ");
				} else {
					ret.append("  ");
				}
				for (int i = 0; i < this.getGamefieldSize(); i++) {
					ret.append("| " + matrix[j][i] + " ");
				}
				ret.append("| ");
				if(this.east[j] != 0) {
					ret.append(this.east[j] + " ");
				} else {
					ret.append("  ");
				}
                ret.append(lineSep + rowSep);
			}
			// append south
			ret.append("  ");
			for (int j = 0; j < this.getGamefieldSize(); j++) {
				ret.append("  ");
				if(this.south[j] != 0) {
					ret.append(this.south[j]);
				} else {
					ret.append(" ");
				}
				ret.append(" ");
			}
			System.out.printf("%s\n", ret.toString());
		}
	}

	/**
	 * Sets a given solution to this instance.
	 *
	 * @param solution the solution to set
	 * @return true if the solution was feasible, false else
	 */
	public boolean setSolution(int[][] solution) {
        boolean feasible = this.solutionIsFeasible(solution);
        if(feasible) {
			this.solution = solution;
		}
		return feasible;
	}

	public boolean solutionIsFeasible(int[][] solution) {
        // TODO: It could be useful to implement this method.
        return true;
	}

}
