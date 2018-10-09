import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import java.util.Random;
import java.lang.Math;
import java.util.LinkedList; 
import java.util.Queue; 

class TramStop extends Switch{

	Queue<Double> queuePassengers = new LinkedList<Double>();
	double timeLastPassengerArrival = 0;
	public double maxWaitingTime=0;
	double[] lambdaArr = new double[64];
	double[] probDep = new double[64];
 	Random rand = new Random(); 
 	public double totPassengers;

	public TramStop(int id, double runtimeMu, double runtimeVar, double runtimeMin, double[] lambdaArr, double[] probDep){
		super(id, runtimeMu, runtimeVar, runtimeMin);
		this.lambdaArr = lambdaArr;
		this.probDep = probDep;

	}
	@Override
	public Departure planDeparture(Tram tram, double timeEvent){
		tram.setLocation(id);

		this.generatePassengers(timeEvent);

		int numPassengers = tram.getNumPassengers();
		int timeSlot = timeSlot(timeEvent);
		int passOut = Math.min(new BinomialDistribution(numPassengers,probDep[timeSlot]).sample(),numPassengers);
		int passIn = 0;
		if (!queuePassengers.isEmpty()){
			maxWaitingTime = Math.max(maxWaitingTime, queuePassengers.peek());
			passIn = Math.min(queuePassengers.size(), 420-numPassengers+passOut);
		}
		double dwellTime = dwellTime(passIn, passOut);
		System.out.println("passengersIn: "+passIn+", passOut: "+ passOut+" QUEUE: "+(queuePassengers.size()));
		for (int i=0;i<passIn;i++){
			queuePassengers.remove();
		}

		//to do: extra instappers toevoegen
		tram.addPassengers(passIn-passOut);
		//to do: wachten trams als ze vooruit lopen op schema?? event.tram.scheduledArr[id]),
		return new Departure(timeEvent+dwellTime,tram,id);
		
	}
	private void generatePassengers(double to){
		double currArrival=timeLastPassengerArrival;
		while (currArrival<to){
			double nextArrival = getNextPassenger(currArrival);

			if (timeSlot(currArrival)==timeSlot(nextArrival)){
				currArrival = nextArrival;
				queuePassengers.add(currArrival);
				totPassengers++;
			}
			else {
				currArrival = (timeSlot(currArrival)+1)*15;
			}
		}
		timeLastPassengerArrival = to;
	}
	 private double getNextPassenger(double time) {
    	return  Math.log(1-rand.nextDouble())/(-lambdaArr[timeSlot(time)])+ time;
	}
	public double dwellTime(int passIn, int passOut){
		return new GammaDistribution(2, 0.4*(12.5+0.22*passIn+0.13*passOut)).sample()/60;
	}

	// to do: dienstregeling toevoegen <- maaike


	// in minuten
	private int timeSlot(double timeEvent){
		return (int)Math.floor(timeEvent/15);
	}

}

