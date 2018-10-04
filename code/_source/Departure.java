public class Departure extends Event{
	int tramStopID;
	public Departure(double timeEvent, Tram tram, int tramStopID){
		super(timeEvent, tram);
		this.tramStopID = tramStopID;
	}
}