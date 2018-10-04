
JC = javac
OUT := bin

.SUFFIXES: .java .class
.java.class:
		$(JC) -d ./bin -cp .:_source/commons-math3-3.6.1/commons-math3-3.6.1.jar:./bin $(JFLAGS) $*.java

CLASSES = \
		_source/Event.java \
		_source/Arrival.java \
		_source/Departure.java \
		_source/TramStop.java \
		_source/Statistics.java \
		_source/Main.java 

default: prog1 classes

classes: $(CLASSES:.java=.class)

prog1: _source/Tram.java
		$(JC) -d ./bin -cp _source/commons-math3-3.6.1/commons-math3-3.6.1.jar _source/Tram.java
clean:
		$(RM) bin/*.class