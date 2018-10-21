import java.util.ArrayList; 

public class Tram {
	// Time tram should leave at uithof(0) and cs(1)
	public double[] scheduledDep;
	int numPassengers = 0;
	public int location = 0;
	int id;
	public int roundsLeft;
	public boolean waitingAtPR=false;

	public Tram(int id, double[] scheduledDep, int rounds){
		this.id=id;
		this.scheduledDep = scheduledDep;
		this.roundsLeft = rounds;
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
		if (location == 20) {
			location = 0;
			this.waitingAtPR=true;
		}
	}

	// returns time tram is supposed to leave location
	public double scheduledDeparture(){
		// When at endstation (9), change to new time // change this when adding more stops!!
		if (this.waitingAtPR) return scheduledDep[0];
		return scheduledDep[location];
	}

	// set a new schedule for the tram
	public void setNewSchedule(double[] scheduledDep){
		this.scheduledDep = scheduledDep;
		//this.id = (int)scheduledDep[1];
		this.waitingAtPR = false;
		this.roundsLeft--;
	}
}