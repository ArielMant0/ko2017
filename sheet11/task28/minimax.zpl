# Find optimal strategy for column player

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
var y[Columns] real >= 0;
var w real;

# Objective function
minimize objective: w;

# Constraints
subto winloss:
    forall <j> in Rows:
        w - sum <i> in Columns : a[j,i]*y[i] >= 0;

subto maxprob1:
    sum <i> in Columns : y[i] <= 1;

subto maxprob2:
    sum <i> in Columns : y[i] >= 1;