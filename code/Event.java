abstract class Event{
	double timeEvent;
	int tramNr
	public Event(double timeEvent){
		this.timeEvent = timeEvent;
	}	
	public double getTime(){
		return timeEvent;
	}
}
class Arrival extends Event{
	public Arrival(double timeEvent){
		super(timeEvent);
	}

}

class Departure extends Event{
	int serverID;
	public Departure(double timeEvent, int serverID){
		super(timeEvent);
		this.serverID = serverID;
	}
}

