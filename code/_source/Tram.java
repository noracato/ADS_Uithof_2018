public class Tram {
	double[] scheduledArr = new double[16]; //assuming one trip is a round-trip starting from Uithof
	int numPassengers;
	int location = 0;
	public Tram(double[] scheduledArr, int numPassengers){
		this.scheduledArr = scheduledArr;
		this.numPassengers = numPassengers;
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
}