public abstract class Event{
	double timeEvent;
	public Tram tram; // evt veranderen in tramID?
	public Event(double timeEvent){
		this.timeEvent = timeEvent;
	}	
	public double getTime(){
		return timeEvent;
	}
	public int getLocation(){
		return tram.getLocation();
	}
}
