import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Deque;
import java.util.Queue; 
import java.lang.Math;

class TramStop{
	public int id;
	boolean idle = true;
	Deque<Tram> queueTram = new LinkedList<Tram>();
	Queue<Double> queuePassengers = new LinkedList<Double>();
	double timeLastDeparture = 0;
	double timeLastArrival = 0;
	public double maxWaitingTime=0;
	double[] lambdaArr = new double[64];
	double[] probDep = new double[64];
 	Random rand = new Random(); 
 	LogNormalDistribution runtimeDist;
 	double runtimeMin;
 	public double totPassengers;

	public TramStop(int id, double[] lambdaArr, double[] probDep, double runtimeMu, double runtimeVar, double runtimeMin){
		this.id = id;
		this.lambdaArr = lambdaArr;
		this.probDep = probDep;
		this.runtimeMin = runtimeMin;
		this.runtimeDist  = new LogNormalDistribution(runtimeMu, runtimeVar);
	}
	public Departure planDeparture(Tram tram, double timeEvent){
			//in case tram has to wait for previous tram to depart
			if (!this.serverIdle(tram)) return null;

			tram.setLocation();
			timeEvent = Math.max(timeEvent, timeLastDeparture+(double)2/3);

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
			
			for (int i=0;i<passIn;i++){
				queuePassengers.remove();
			}

			// extra passengers:
			double expectedDeparture = timeEvent+dwellTime;
			int passExtra = 0;
			this.generatePassengers(expectedDeparture);
			if (!queuePassengers.isEmpty()){
				passExtra = Math.min(queuePassengers.size(), 420-numPassengers+passOut-passIn);
				for (int i=0;i<passExtra;i++){
					queuePassengers.remove();
				}
				if (tram.getLocation() != 1 && tram.getLocation() != 9) expectedDeparture += dwellTime(passExtra, 0);
			}

			tram.addPassengers(passIn+passExtra-passOut);
			
			System.out.println("passengersIn: "+passIn+", passOut: "+ passOut+", passExtra: "+ passExtra+" QUEUE: "+queuePassengers.size());

			//to do: wachten trams als ze vooruit lopen op schema?? event.tram.scheduledArr[id]),
						// if (timeSlot<4 || (timeSlot>11 && timeSlot <40) || timeSlot > 47){
			// 	departureTime = Math.max(departureTime, tram.scheduledDep[id]);
			// }

			// trammertje kijkt of het te laat is
			double scheduledDep = tram.schelduledDeparture();
			if (expectedDeparture > scheduledDep) System.out.println("Tram "+tram.id+" "+(expectedDeparture-scheduledDep) +" minuten achter op schema");
			else{
				System.out.println("Tram "+ tram.id+ " loopt voor op schema"+ scheduledDep);
				expectedDeparture = scheduledDep;
			}

			return new Departure(expectedDeparture, tram, id);
		
	}
	public Arrival planArrival(double timeEvent, Tram tram){

		double runtime = runtimeDist.sample();
		runtime = Math.max(runtime, runtimeMin);
		if (id == 4 || id == 9) runtime --; // after switch
		//System.out.println("niet in de rij: tram "+tram.id+", time: "+timeEvent+", aankomst: "+(timeEvent+runtime));
		double arrivalTime = Math.max(timeLastArrival,timeEvent+runtime);
		timeLastArrival = arrivalTime+0.000001;
		return new Arrival(arrivalTime, tram);
	}
	public boolean serverIdle(Tram tram){
		if (!this.idle){
			queueTram.addLast(tram);
			System.out.println("in rij voor stop "+id+": tram "+tram.id);
			return false;
		}
		//tramstop available, schedule departure
		this.idle=false;
		return true;
	}
	public Tram nextTramInQueue(){
		return queueTram.poll();
	} 	
	private void generatePassengers(double to){
		double currArrival=timeLastDeparture;
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
		timeLastDeparture = to;
	}
	 private double getNextPassenger(double time) {
    	return  Math.log(1-rand.nextDouble())/(-lambdaArr[timeSlot(time)])+ time;
	}

	public double dwellTime(int passIn, int passOut){
		return new GammaDistribution(2, 0.4*(12.5+0.22*passIn+0.13*passOut)).sample()/60;
	}

	// in minuten
	private int timeSlot(double timeEvent){
		return (int)Math.floor(timeEvent/15);
	}
	public void setIdle(Tram tram){
		this.idle = true;
	}
}

