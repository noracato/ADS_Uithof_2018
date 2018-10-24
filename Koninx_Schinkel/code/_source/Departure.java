public class Departure extends Event{
	public Departure(double timeEvent, Tram tram){
		super(timeEvent);
		this.tram=tram;
	}
}