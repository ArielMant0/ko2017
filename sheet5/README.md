# Simulated Annealing

Alle Parameter für den Algorithmus befinden sich in der Datei *SimulatedAnnealing.java*

Parameter:
 + **Iterationen** setzte *MAX_ITERATION* (default 500000)
 + **Startlösung** setzte *INITAL* auf
   + 0 => Sortiere absteigend nach Kosteneffizienz und nehme X erste Items mit (default)
   + 1 => Sortiere aufsteigend nach Gewicht und nehme X erste Items mit
   + sonst => Nehme X zufällige Items mit
 + **Abkühlung** setzte *DENOM* auf
   + 0 => i^2
   + sonst => t_0 = *MAX_ITERATION*/2, t_i = t_i-1 * *ALPHA* (default *ALPHA* = 0.9)
