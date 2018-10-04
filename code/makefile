
JC = javac
OUT := bin
CL=.:_source/commons-math3-3.6.1/commons-math3-3.6.1.jar:./bin

ifdef SYSTEMROOT
	CL=.;_source/commons-math3-3.6.1/commons-math3-3.6.1.jar;./bin
endif

.SUFFIXES: .java .class

.java.class:
		$(JC) -d ./bin -cp $(CL) $*.java

CLASSES = \
		_source/Tram.java \
		_source/Event.java \
		_source/Arrival.java \
		_source/Departure.java \
		_source/TramStop.java \
		_source/Statistics.java \
		_source/Main.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) bin/*.class