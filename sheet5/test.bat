@echo OFF
SET filename=%1
SET iterations=%2
SET command=java simulatedAnnealing.Main

@echo Compiling Simulated Annealing Package

javac simulatedAnnealing\*.java

@echo Doing %iterations% iterations for file %filename%

IF EXIST result.txt DEL result.txt

FOR /L %%G IN (1, 1, %iterations%) DO (
    %command% %filename% >> result.txt
)

@echo Done
