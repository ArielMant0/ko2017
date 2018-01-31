# Find optimal strategy for row player

# Row and Column definitions
set Columns := { 0 .. 2 };
set Rows := { "00", "01", "10", "11" };

# Parameter (game matrix)
param a[Rows*Columns] :=
             |  0,  1,  2 |
        |"00"|  0,  0,  0 |
        |"01"| -1,  1, -1 |
        |"10"| -1,  1,  1 |
        |"11"|  0, -2, -2 |;

# Variables
var x[Rows] real >= 0;
var z real;

# Objective function
maximize objective: z;

# Constraints
subto winloss:
    forall <j> in Columns:
        z - sum <i> in Rows : a[i,j]*x[i] <= 0;

subto maxprob1:
    sum <i> in Rows : x[i] <= 1;

subto maxprob2:
    sum <i> in Rows : x[i] >= 1;