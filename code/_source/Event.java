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
