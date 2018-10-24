This simulation project was developed by Maaike Koninx and Nora Schinkel.
ADS, October 2018

SETUP
_source contains all the .java files
bin contains the full build and output 

RUN
Ranges for q, spitsfreq (rush hour), dayfreq (day hours) and dalfreq (off-peak hours) can be set at the top of the main file.
To run the simulation 100 times for set or varying schedules, please use:

java -cp .:_source/commons-math3-3.6.1/commons-math3-3.6.1.jar:./bin Main

BUILD
To build, please use the make file (type make in terminal). To build and run, make use of the make run command.