import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Queue; 
import java.lang.Math;

class TramStop{
	public int id;
	private boolean idle = true;
	Queue<Tram> queueTram = new LinkedList<Tram>();
	Queue<Double> queuePassengers = new LinkedList<Double>();
	double timeLastPassengerArrival = 150;
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
			//System.out.println("passengersIn: "+passIn+", passOut: "+ passOut+" QUEUE: "+(queuePassengers.size()-1));
			for (int i=0;i<passIn;i++){
				queuePassengers.remove();
			}

			//to do: extra instappers toevoegen
			tram.addPassengers(passIn-passOut);
			//to do: wachten trams als ze vooruit lopen op schema?? event.tram.scheduledArr[id]),
			return new Departure(timeEvent+dwellTime,tram,id);
		
	}
	public Arrival planArrival(double timeEvent, Tram tram){
		if (!this.serverIdle(tram)) return null;

		double runtime = runtimeDist.sample();
		runtime = Math.max(runtime, runtimeMin);
		//System.out.println("niet in de rij: tram "+tram.id+", time: "+timeEvent+", aankomst: "+(timeEvent+runtime));
		return new Arrival(timeEvent+runtime, tram);
	}
	public boolean serverIdle(Tram tram){
		if (!this.idle){
			queueTram.add(tram);
			System.out.println("in de rij: tram "+tram.id);
			return false;
		}
		else {
			this.idle = false;
			return true;
		}
	}
	public Tram nextTramInQueue(){
		return queueTram.poll();
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

	// in minuten
	private int timeSlot(double timeEvent){
		return (int)Math.floor(timeEvent/15);
	}
	public void setIdle(int tramId){
		this.idle = true;
	}
}

