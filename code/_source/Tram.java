import java.util.ArrayList; 

public class Tram {
	// Time tram should leave at uithof(0) and cs(1)
	double[] scheduledDepEnds;
	int numPassengers = 0;
	int location = 0;
	int id;
	public double[] scheduledDepStops = {17, 0, 0, 2.1521, 3.771, 5.456, 6.775, 8.7604, 10.0625, 14.4313
									, 17, 0, 0, 2.5375, 6.8917, 8.1792, 10.1667, 11.4708, 13.2083333333333,14.8125};


	public Tram(int id, double[] scheduledDepEnds){
		this.id=id;
		this.scheduledDepEnds = scheduledDepEnds;
	}
	public void addPassengers(int numPassengers)
	{
		this.numPassengers += numPassengers;
	}
	public int getNumPassengers(){
		return this.numPassengers;
	}
	public int getLocation(){
		return location;
	}
	public void setLocation(){
		this.location++;
		if (location == 20) location = 0;
	}

	// returns time tram is supposed to leave location
	public double schelduledDeparture(){
		// When at endstation (9), change to new time // change this when adding more stops!!
		if (location < 11){
			return scheduledDepStops[location]+scheduledDepEnds[0];
		}
		return scheduledDepStops[location]+scheduledDepEnds[1];
	}

	// set a new schedule for the tram
	public void setNewSchedule(double[] scheduledDepEnds){
		this.scheduledDepEnds = scheduledDepEnds;
	}
}