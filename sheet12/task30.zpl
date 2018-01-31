# Task 30 LP

# Variables
var x_1 integer >= 0;
var x_2 integer >= 0

# Objective Function
maximize objective: 
    17 * x_1 + 12 * x_2;

# Constraints
subto con1:
    10 * x_1 + 7 * x_2 <= 40;

subto con2:
    x_1 + x_2 <= 5;