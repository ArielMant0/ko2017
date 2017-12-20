package tsp;

public class Test {

    public static final byte MAX_CONFIG = 3;

    public static void main(String[] args) {
        if (args.length > 0) {
            // Read Instance
            Ant.TspInstance instance = new Ant.TspInstance(args[0]);
            
            // Whether to try all hyperparameter configuration
            boolean tryAll = false;
            // Set iterations
            int times = 50;
            try {
                if (args.length > 1)
                    times = Integer.parseInt(args[1]);
                if (args.length > 2)
                    tryAll = Integer.parseInt(args[2]) == 1;
            } catch (Exception e) {
                System.out.println("Invalid input parameter");
            }

            int[] sol;
            byte config = 0;
            double avg = 0;
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE, tmp = 0;

            for (; config <= MAX_CONFIG; config++) {
                AntSolver.config(config);
                avg = 0;
                tmp = 0;
                min = Integer.MAX_VALUE;
                max = Integer.MIN_VALUE;
                for (int i = 0; i < times; i++) {
                    sol = AntSolver.solve(instance);
                    tmp = Ant.getCost(instance, sol);

                    if (tmp > max)
                        max = tmp;
                    if (tmp < min)
                        min = tmp;

                    avg += tmp;
                }
                avg /= times;

                System.out.println("CONFIG(" + config + ") =>\n\tavg: " + avg  + "\n\tworst: " + min  +  "\n\tbest: " + max);
            }
        }
    }
}