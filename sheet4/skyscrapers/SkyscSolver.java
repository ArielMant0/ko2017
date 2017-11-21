package skyscrapers;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.io.IOException;
import java.util.List;

/**
 * A skyscrapers solver.
 *
 * @author
 */
public class SkyscSolver {

    public static void main(String args[]) throws IOException {
        if(args != null && args.length > 0) {
            System.out.println("Read file "+ args[0] + ".");
            Instance instance = Reader.readSkyscInstance(args[0]);
            System.out.println("Gamefield:");
            instance.printGamefield();
            long start = System.currentTimeMillis();
            SkyscSolver.solve(instance);
            long end = System.currentTimeMillis();
            System.out.printf("time = %.3fs\n", (end - start) / 1000.0);
        } else {
            System.out.println("Please enter a skyscrapers file.");
        }
    }

    public static void solve(Instance instance) {
        // 1. Create model
        Model model = new Model("Skyscraper");

        int[][] field = instance.getGamefield();
        int size = instance.getGamefieldSize();
        // Values for north, east, south and west
        int[] nvals = instance.getNorth();
        int[] evals = instance.getEast();
        int[] svals = instance.getSouth();
        int[] wvals = instance.getWest();

        // 2. Create variables
        IntVar x[][] = model.intVarMatrix(size, size, 1, size);
        // Whether x[i,j] can be seen from north, east, south or west
        BoolVar north[][] = model.boolVarMatrix(size, size);
        BoolVar east[][] = model.boolVarMatrix(size, size);
        BoolVar south[][] = model.boolVarMatrix(size, size);
        BoolVar west[][] = model.boolVarMatrix(size, size);

        // 3. Add constraints
        for (int i = 0; i < size; i++) {
            // All number in a row must be different
            model.allDifferent(x[i]).post();

            // We can always see the first skyscraper
            model.and(north[0][i], east[i][size-1], south[size-1][i], west[i][0]).post();

            // Sum over N/E/S/W row must be identical to given the number 
            if (evals[i] > 0) model.sum(east[i], "=", evals[i]).post();
            if (wvals[i] > 0) model.sum(west[i], "=", wvals[i]).post();
            
            BoolVar tmp[] = new BoolVar[size];
            if (nvals[i] > 0) {
                for (int k = 0; k < size; k++) {
                    tmp[k] = north[k][i];
                }
                model.sum(tmp, "=", nvals[i]).post();
            }
            if (svals[i] > 0) {
                for (int k = 0; k < size; k++) {
                    tmp[k] = south[k][i];
                }
                model.sum(tmp, "=", svals[i]).post();
            }

            for (int j = 0; j < size; j++) {
                // Fixed fields
                if (field[i][j] > 0)
                    model.arithm(x[i][j], "=", field[i][j]).post();

                // We can always see the biggest skyscraper
                model.ifThen(
                    model.arithm(x[i][j], "=", size),
                    model.and(north[i][j], east[i][j], south[i][j], west[i][j])
                );

                // Vertical rows are all made up of different values
                model.arithm(x[(i+size+1)%size][j], "!=", x[i][j]).post();

                // North
                for (int k = 0; k < i; k++) {
                    // x[k][j] < x[i][j] <=> north[i][j]
                    model.reifyXltY(x[k][j], x[i][j], north[i][j]);
                }
                // West
                for (int k = 0; k < j; k++) {
                    model.reifyXltY(x[i][k], x[i][j], west[i][j]);
                }
                // South
                for (int k = i+1; k < size; k++) {
                    model.reifyXltY(x[k][j], x[i][j], south[i][j]);
                }
                // East
                for (int k = j+1; k < size; k++) {
                    model.reifyXltY(x[i][k], x[i][j], east[i][j]);
                }
            }
        }

        // 4. Get solver and solve model
        Solver solver = model.getSolver();
        List<Solution> solutions = solver.findAllSolutions();

        solver.printStatistics();

        // 5. Print solutions
        System.out.println("Number of solutions: " + solutions.size());
        int cnt = 1;
        for (Solution solution : solutions) {
            int[][] solutionArray = new int[size][size];
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    solutionArray[i][j] = solution.getIntVal(x[i][j]);
                }
            }
            instance.setSolution(solutionArray);
            System.out.println("------- solution number " + cnt++ + "-------");
            instance.printSolution();
        }
    }
}