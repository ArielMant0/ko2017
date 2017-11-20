Aufgabe P3

Um den Solver für Teilaufgabe a) auszuprobieren, muss in Main.java der erste Solver-Aufruf
einkommentiert werden.
Um den Solver für Teilaufgaben b) und c) zu verwenden, muss in Main.java der zweite Solver-
Aufruf einkommentiert werden.

Die Lösungen der Teilaufgaben b) und c) wurden alle in der Klasse "ChocoSolver" untergebracht.
Es stehen dafür drei Methoden zu Verfügung:
    1. withKnapsack(instance) für b)
    2. withoutKnapsack(instance) für b)
    3. withSolve(instance) für c)
Um die jeweils gewünschte Methode auszuführen, muss in der oben genannten 
Klasse die Methode "solve(instance)" so wie unten aufgeführt angepasst werden.

a)

Die Lösung für diese Teilaufgabe befindet sich in der Klasse BnBBinarySolverAlt.java.

b)

Für die erste Variante muss die Zeile "return withKnapsack(instance)" ausgeführt werden.
Die andere Variante kann durch die Zeile "return withoutKnapsack(instance)" ausgeführt werden.

c)

Hierfür muss die Zeile "return withSolve(instance)" einkommentiert und ausgeführt werden.