# Simulated Annealing

Alle Parameter für den Algorithmus befinden sich in der Datei *SimulatedAnnealing.java*

Parameter:
 + **Iterationen** setze *MAX_ITERATION* (default 500000)
 + **Startlösung** setze *INITAL* auf
   + 0 => Sortiere absteigend nach Kosteneffizienz und nehme X erste Items mit (default)
   + 1 => Sortiere aufsteigend nach Gewicht und nehme X erste Items mit
   + sonst => Nehme X zufällige Items mit
 + **Abkühlung** setzte *DENOM* auf
   + 0 => i^2
   + sonst => c / log(i+1), c = MAX_ITERATIONEN / 2
