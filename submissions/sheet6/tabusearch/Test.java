package tabusearch;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        String filename;
        int iterations = 100;

        if (args.length > 0) {
            filename = args[0];
            if (args.length > 1)
                iterations = Integer.parseInt(args[1]);

            int[] values = new int[iterations];
            int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
            double mean = 0.0;

            Instance instance = Reader.readInstance(filename);
            TabuSearch solver = new TabuSearch();

            int last = filename.lastIndexOf('\\');
            filename = filename.substring(last+1, filename.length());
            System.out.println("Doing " + iterations + " iterations for file: " + filename);

            long start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                if (System.currentTimeMillis() - start >= 300000) {
                    System.out.println("Time limit reached");
                    break;
                }

                Solution solution = solver.solve(instance);
                if (!solution.isFeasible()) {
                    System.out.format("Infeasible solution: iteration = %d, weight = %d, capacity = %d\n", i, solution.getWeight(), instance.getCapacity());
                    i--;
                } else if (!solution.isBinary()) {
                    System.out.println("Non binary solution: iteration = " + i);
                    i--;
                }else {
                    values[i] = solution.getValue();
                    mean += values[i];

                    if (values[i] > max)
                        max = values[i];
                    else if (values[i] < min)
                        min = values[i];
                }
            }
            mean /= iterations;

            System.out.println("\nResults:\n\tmax  = " + max);
            System.out.println("\tmin  = " + min);
            System.out.println("\tmean = " + mean);
        }
    }
}