# Solve binary knapsack instances, task P9

# Input file
param file := "instances/rucksack0010.txt";

# Parameters
param n := read file as "1n" skip 0 use 1 comment "#";
set N := {1 to n};

param values[N] := read file as "1n" skip 1 use n comment "#";
param weights[N] := read file as "2n" skip 1 use n comment "#";
param capacity := read file as "1n" skip 1+n use 1 comment "#";

# Variables
var x[N] binary;

# Objective
maximize objective:
    sum <i> in N: x[i] * values[i];

# Constraints
# Don't go over the capacity of the knapsack
subto maxWeight:     
    sum <i> in N: x[i] * weights[i] <= capacity;
