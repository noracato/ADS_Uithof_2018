public abstract class Event{
	double timeEvent;
	Tram tram; // evt veranderen in tramID?
	public Event(double timeEvent, Tram tram){
		this.timeEvent = timeEvent;
		this.tram = tram;
	}	
	public double getTime(){
		return timeEvent;
	}
}
class Arrival extends Event{
	public Arrival(double timeEvent, Tram tram){
		super(timeEvent, tram);
	}

}

class Departure extends Event{
	int tramStopID;
	public Departure(double timeEvent, Tram tram, int tramStopID){
		super(timeEvent, tram);
		this.tramStopID = tramStopID;
	}
}

class Tram{
	double[] scheduledArr = new double[16]; //assuming one trip is a round-trip starting from Uithof
	int numPassengers;
	int location = 0;
	public Tram(double[] scheduledArr, int numPassengers){
		this.scheduledArr = scheduledArr;
		this.numPassengers = numPassengers;
	}
	public void addPassengers(int numPassengers)
	{
		this.numPassengers += numPassengers;
	}
	public int getNumPassengers(){
		return this.numPassengers;
	}
}
