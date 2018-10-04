
JC = javac
OUT := bin

.SUFFIXES: .java .class
.java.class:
		$(JC) -d ./bin -cp _source/commons-math3-3.6.1/commons-math3-3.6.1.jar $(JFLAGS) $*.java

CLASSES = \
		_source/Event.java \
		_source/TramStop.java \
		_source/Statistics.java \
		Main.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class