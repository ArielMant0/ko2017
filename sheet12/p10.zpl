# P10 - Min-Cost-Flow Problem

# Read parameters from file
param file := "instances/fluss1.txt";

param n := read file as "1n" skip 0 use 1 comment "#";
set N := { 1 .. n };
param u[N*N] := read file as "n+" skip 1 use n comment "#";
param c[N*N] := read file as "n+" skip n+1 use n comment "#";
param s[N] := read file as "n+" skip 2*n+1 use 1 comment "#";


# Variables
var x[N*N] real >= 0;

# Objective
minimize objective:
    sum <i,j> in N*N : c[i,j] * x[i,j];

# Constraints
subto capacity:
    forall <i,j> in N*N:
        x[i,j] <= u[i,j];

subto flowLess:
    forall <i> in N:
        sum <j> in N: x[i,j] - sum <k> in N: x[k,i] <= s[i];

subto flowGreater:
    forall <i> in N:
        sum <j> in N: x[i,j] - sum <k> in N: x[k,i] >= s[i];
