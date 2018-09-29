import org.apache.commons.math3.distribution.GammaDistribution;

class TramStop{
	int id;
	private boolean idle = true;
	Queue<Event> queueTram = new LinkedList<Event>();
	Queue<Double> queuePassengers = new LinkedList<Double>();
	double timeLastArrivalPassenger = 0;
	double maxWaitingTime=0;
	double[] lambdaArr = new double[64];
	double[] lambdaDep = new double[64];
 	Random rand = new Random(); 

	public TramStop(int id, double[] lambdaArr, double[] lambdaDep){
		this.id = id;
		this.lambdaArr = lambdaArr;
		this.lambdaDep = lambdaDep;
	}
	public Departure planDeparture(Event event){
		if (!this.idle){
			queueTram.add(event);
			//return NULL if event in queue
		}
		else {
			this.idle = false;
			queuePassengers.add(generatePassengers(lambdaArr, event.timeEvent));
			maxWaitingTime = Math.max(maxWaitingTime, queuePassengers.peek());

			double numPassengers = event.tram.getNumPassengers();
			double passOut = Math.min(generatePassengers(lambdaDep, event.timeEvent).size()-1,numPassengers);
			//!! to do: stappen nu alle passagiers uit?
			double passIn = Math.min(queuePassengers.size()-1, 420-numPassengers+passOut);
			double dwellTime = new GammaDistribution(2, 0.4*(12.5+0.22*passIn+0.13*passOut)).sample();
			for (int i=0;i<passIn;i++){
				queuePassengers.remove();
			}
			//to do: extra instappers toevoegen
			event.tram.addPassengers(passIn-passOut);
			return new Departure(event.timeEvent+dwellTime,event.tram,id);
		}
	}
	public void makeAvailable(){
		this.idle = true;
	}
	private LinkedList<Double> generatePassengers(double[] lambda, double to){
		double currArrival=timeLastArrivalPassenger;
		Queue<Double> currQueue = new LinkedList<Double>();
		while (currArrival<to){
			int hour = floor(currArrival);
			int timeSlot = (hour - 6) * 4 + floor((currArrival-hour)/0.15);
			currArrival = getNextPassenger(lambda[timeSlot])+currArrival;
			currQueue.add(currArrival);
		}
		return currQueue;
		//to do: instappers na timeEvent
	}
	 private double getNextPassenger(double lambda) {
    	return  Math.log(1-rand.nextDouble())/(-lambda);
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