import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;


class TramStop{
	int id;
	private boolean idle = true;
	Queue<Event> queueTram = new LinkedList<Event>();
	Queue<Double> queuePassengers = new LinkedList<Double>();
	double timeLastArrivalPassenger = 0;
	double[] lambdaArr = new double[64];
	double[] lambdaDep = new double[64];
 	Random rand = new Random(); 

	public TramStop(int id, double[] lambdaArr, double[] lambdaDep){
		this.id = id;
		this.lambdaArr = lambdaArr;
		this.lambdaDep = lambdaDep;
	}
	public Event estimateDeparture(Event event){
		if (!this.idle){
			queueTram.add(event);
			//return NULL if event in queue
		}
		else {
			this.idle = false;
			double scale=0.4*(12.5+0.22*passIn+0.13*passOut)
			//to do: trammetje met #passengers
			double sample = new GammaDistribution(2, scale).sample();
			// return estimated departure time
		}
	}
	public void makeAvailable(){
		this.idle = true;
	}
	private void generatePassengers(double timeEvent){
		currArrival=timeLastArrivalPassenger;
		while (currArrival<timeEvent){
			int hour = floor(currDeparture);
			int timeSlot = (hour - 6) * 4 + floor((currArrival-hour)/0.15);
			double currArrival = getNextPassenger(lambdaArr[timeSlot])+currArrival;
			queuePassengers.add(currArrival);
		}
		timeLastArrivalPassenger = currArrival;
		//to do: instappers na timeEvent
	}
	 private double getNextPassenger(double lambda) {
    	return  Math.log(1-rand.nextDouble())/(-lambda);
	}
}

