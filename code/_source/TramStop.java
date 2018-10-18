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
	double[] lambdaArr = new double[64];
	double[] probDep = new double[64];
 	Random rand = new Random(); 
 	LogNormalDistribution runtimeDist;
 	double runtimeMin;
 	public int totPassengers = 0;
 	public int totLeaving = 0;
 	public double maxWaitingTime=0;
 	public int maxQueueLength=0;
 	public double timeMaxQueue=0;
 	public double timeMaxWait=0;


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
			this.totLeaving+=passOut;
			int passIn = 0;
			if (!queuePassengers.isEmpty() && tram.getLocation()!=1){
				maxWaitingTime = Math.max(maxWaitingTime, timeEvent - queuePassengers.peek());
				passIn = Math.min(queuePassengers.size(), 420-numPassengers+passOut);
			}
			double dwellTime = dwellTime(passIn, passOut);

			// extra passengers:
			double departureTime = timeEvent+dwellTime;
			int passExtra = 0;
			this.generatePassengers(departureTime);
			if (!queuePassengers.isEmpty() && tram.getLocation()!=1){
				passExtra = Math.min(queuePassengers.size()-passIn, 420-numPassengers+passOut-passIn);	
				if (tram.getLocation() != 11) departureTime += dwellTime(passExtra, 0);
			}

			if(id==10)System.out.println(queuePassengers.size()+ " in de rij");

			tram.addPassengers(passIn+passExtra-passOut);
			for (int i=0;i<passIn+passExtra;i++){
					queuePassengers.remove();
				}
			System.out.println("passengersIn: "+passIn+", passOut: "+ passOut+", passExtra: "+ passExtra+" QUEUE: "+queuePassengers.size());

			// trammertje kijkt of het te laat is
			if (timeSlot<4 || (timeSlot>11 && timeSlot <40) || timeSlot > 47){
				double vertraging = departureTime - tram.scheduledDeparture();
				if (vertraging>0) System.out.println("TRAM "+tram.id+": VERTRAAGD: "+vertraging+" minuten");
			 	departureTime = Math.max(departureTime, tram.scheduledDeparture());
			}



			return new Departure(departureTime, tram);
		
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
			//System.out.println("in rij voor stop "+id+": tram "+tram.id);
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
		maxQueueLength = Math.max(maxQueueLength,queuePassengers.size());
		timeMaxQueue = to;
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

