import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Queue; 
import java.lang.Math; 

class TramStop{
	int id;
	private boolean idle = true;
	Queue<Event> queueTram = new LinkedList<Event>();
	Queue<Double> queuePassengers = new LinkedList<Double>();
	double timeLastPassengerArrival = 0;
	double maxWaitingTime=0;
	double[] lambdaArr = new double[64];
	double[] lambdaDep = new double[64];
 	Random rand = new Random(); 

	public TramStop(int id, double[] lambdaArr, double[] lambdaDep){
		this.id = id;
		this.lambdaArr = lambdaArr;
		this.lambdaDep = lambdaDep;
	}
	public Departure planDeparture(Event event, double timeEvent){
		if (!this.idle){
			queueTram.add(event);
			//return NULL if event in queue
		}
		else {
			this.idle = false;
			this.generatePassengers(timeEvent);
			maxWaitingTime = Math.max(maxWaitingTime, queuePassengers.peek());

			int numPassengers = event.tram.getNumPassengers();
			int timeSlot = timeSlot(timeEvent);
			int passOut = Math.min(new BinomialDistribution(numPassengers,lambdaDep[timeSlot]).sample(),numPassengers);
			//!! to do: stappen nu alle passagiers uit?
			int passIn = Math.min(queuePassengers.size()-1, 420-numPassengers+passOut);
			double dwellTime = new GammaDistribution(2, 0.4*(12.5+0.22*passIn+0.13*passOut)).sample();
			for (int i=0;i<passIn;i++){
				queuePassengers.remove();
			}
			//to do: extra instappers toevoegen
			event.tram.addPassengers(passIn-passOut);
			//to do: wachten trams als ze vooruit lopen op schema??
			return new Departure(Math.max(timeEvent+dwellTime,event.tram.scheduledArr[id]),event.tram,id);
		}
	}
	public void makeAvailable(double timeEvent){
		if (!queueTram.isEmpty()){
			planDeparture(queueTram.poll(), timeEvent);
		}
		else this.idle = true;
	}
	private void generatePassengers(double to){
		double currArrival=timeLastPassengerArrival;
		while (currArrival<to){
			currArrival = getNextPassenger(lambdaArr[timeSlot(currArrival)])+currArrival;
			queuePassengers.add(currArrival);
		}
		timeLastPassengerArrival = currArrival;
		//to do: instappers na timeEvent
	}
	 private double getNextPassenger(double lambda) {
    	return  Math.log(1-rand.nextDouble())/(-lambda);
	}
	private int timeSlot(double timeEvent){
		int hour = (int)Math.floor(timeEvent);
		return (hour - 6) * 4 + (int)Math.floor((timeEvent-hour)/0.15);
	}
}

class Tram{
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
}

