# Solve task 21 

# Parameters
set N := {1..3};
param work[N] := <1> 3, <2> 2, <3> 1;
param materials[N] := <1> 1.2, <2> 0.6, <3> 1.4;
param profits[N] := <1> 30, <2> 45, <3> 60;

param bigM := 300;

# Variables
var hemden integer >= 0;
var roecke integer >= 0;
var hosen integer >= 0;
var maschine1 binary;
var maschine2 binary;

# Variables for b)
var machRoecke binary;
var machHosen binary;

# Objective
maximize profit:
    30 * hemden + 45 * roecke + 60 * hosen - maschine1 * 1000 - maschine2 * 2000;

# Constraints
subto materialLimit:
    hemden * materials[1] + roecke * materials[2] + hosen * materials[3] <= 160;

subto workLimit:
    hemden * work[1] + roecke * work[2] + hosen * work[3] <= 150;

subto hemdenMaschine1:
    maschine1 * bigM >= hemden;    

subto roeckeMaschine2:
    maschine2 * bigM >= roecke;

subto hosenMaschine2:
    maschine2 * bigM >= hosen;

# Additional constraints for b)

#subto wennRoecke:
#    machRoecke * bigM >= roecke;

#subto wennHosen:
#    machHosen * bigM >= hosen;

#subto min35Roecke:
#    roecke >= machRoecke * 35;

#subto min35Hosen:
#    hosen >= machHosen * 35;    
