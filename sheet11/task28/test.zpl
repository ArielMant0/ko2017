# Test Player A
#set N := { 1 .. 4};

#var x[N] real >= 0;
#var z real;

# Objective function
#maximize objective: z;

# Constraints
#subto a:
#    z+x[2]+x[3] <= 0;

#subto b:
#    z-x[2]-x[3]+2*x[4] <= 0;

#subto c:
#    z+x[2]-x[3]+2*x[4] <= 0;

#subto d1:
#    x[1]+x[2]+x[3]+x[4] <= 1;

#subto d2:
#    x[1]+x[2]+x[3]+x[4] >= 1;

# Test Player B
set N := { 1 .. 3 };

var x[N] real >= 0;
var z real;

# Objective function
minimize objective: z;

# Constraints
subto a:
    z >= 0;

subto b:
    z+x[1]-x[2]+x[3] >= 0;

subto c:
    z+x[1]+x[2]-x[3] >= 0;

subto d:
    z+2*x[2]+2*x[3] >= 0;

subto one1:
    x[1]+x[2]+x[3] <= 1;

subto one2:
    x[1]+x[2]+x[3] >= 1;