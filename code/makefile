
JC = javac
OUT := bin
CL=.:_source/commons-math3-3.6.1/commons-math3-3.6.1.jar:./bin

# windows check
ifeq ($(shell echo "check_quotes"),"check_quotes")
	CL=.;_source/commons-math3-3.6.1/commons-math3-3.6.1.jar;./bin
	RM = del /Q
endif

.SUFFIXES: .java .class

.java.class:
		$(JC) -d ./bin -cp $(CL) $*.java

CLASSES = \
		_source/Tram.java \
		_source/Event.java \
		_source/Arrival.java \
		_source/Departure.java \
		_source/ArrivalUithof.java \
		_source/TramStop.java \
		_source/Eindhalte.java \
		_source/Statistics.java \
		_source/Main.java 

default: classes

classes: $(CLASSES:.java=.class)

run: classes
	java -cp $(CL) Main

clean:
		$(RM) bin/*.class