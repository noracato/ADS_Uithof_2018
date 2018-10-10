public class Tram {
	//double[] scheduledArr = new double[16]; //assuming one trip is a round-trip starting from Uithof
	int numPassengers = 0;
	int location = 0;
	int id;
	public double[] scheduledDep;
	public Tram(int id, double[] scheduledDep){
		this.id=id;
		this.scheduledDep = scheduledDep;
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
		if (location == 16) location = 0;
	}
}