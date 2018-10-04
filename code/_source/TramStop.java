import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
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
	double[] probDep = new double[64];
 	Random rand = new Random(); 
 	LogNormalDistribution runtimeDist;

	public TramStop(int id, double[] lambdaArr, double[] probDep, double runtimeMu){
		this.id = id;
		this.lambdaArr = lambdaArr;
		this.probDep = probDep;
		//to do: aanpassen runtimeVar
		this.runtimeDist  = new LogNormalDistribution(runtimeMu, 0.00001);
	}
	public Departure planDeparture(Event event, double timeEvent){
		if (!this.idle){
			queueTram.add(event);
			return null;
		}
		else {
			this.idle = false;
			event.tram.setLocation(id);

			this.generatePassengers(timeEvent);
			maxWaitingTime = Math.max(maxWaitingTime, queuePassengers.peek());

			int numPassengers = event.tram.getNumPassengers();
			int timeSlot = timeSlot(timeEvent);
			int passOut = Math.min(new BinomialDistribution(numPassengers,probDep[timeSlot]).sample(),numPassengers);
			int passIn = Math.min(queuePassengers.size()-1, 420-numPassengers+passOut);
			double dwellTime = new GammaDistribution(2, 0.4*(12.5+0.22*passIn+0.13*passOut)).sample()/60;
			System.out.println("passengersIn: "+passIn+", passOut: "+ passOut+" QUEUE: "+(queuePassengers.size()-1));
			for (int i=0;i<passIn;i++){
				queuePassengers.remove();
			}
			//to do: extra instappers toevoegen
			event.tram.addPassengers(passIn-passOut);
			//to do: wachten trams als ze vooruit lopen op schema?? event.tram.scheduledArr[id]),
			return new Departure(timeEvent+dwellTime,event.tram,id);
		}
	}
	public Arrival planArrival(double timeEvent, Tram tram){
		this.idle = true;
		double runtime = runtimeDist.sample();
		return new Arrival(timeEvent+runtime, tram);
	}
	public Event nextTramInQueue(){
		return queueTram.poll();
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

	// in minuten
	private int timeSlot(double timeEvent){
		return (int)Math.floor(timeEvent/15);
	}
}

