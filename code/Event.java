abstract class Event{
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

