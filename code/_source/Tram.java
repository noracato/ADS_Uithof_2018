public class Tram {
	//double[] scheduledArr = new double[16]; //assuming one trip is a round-trip starting from Uithof
	int numPassengers = 0;
	int location = -1;
	public Tram(){
		//this.scheduledArr = scheduledArr;
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
	public void setLocation(int id){
		this.location = id;
	}
}