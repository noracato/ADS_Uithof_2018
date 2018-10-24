import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Deque;
import java.util.Queue; 
import java.lang.Math;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


class TramStop{
	public int id;
	public boolean idle = true;
	Deque<Tram> queueTram = new LinkedList<Tram>();
	public Queue<Double> queuePassengers = new LinkedList<Double>();
	double timeLastDeparture = 0;
	double timeLastArrival = 0;
	double[] lambdaArr = new double[64];
	double[] probDep = new double[64];
 	Random rand = new Random(); 
 	LogNormalDistribution runtimeDist;
 	double runtimeMin;
 	Statistics stats = new Statistics();
	public double avgWait=0;

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

			if (id!=0 && id!=10) timeEvent = Math.max(timeEvent, timeLastDeparture+(double)2/3);


			int numPassengers = tram.getNumPassengers();
			int timeSlot = timeSlot(timeEvent);
			int passOut = Math.min(new BinomialDistribution(numPassengers,probDep[timeSlot]).sample(),numPassengers);

			int passIn = 0;
			int currOut=passOut;
			double departureTime = timeEvent;
			if (!tram.waitingAtPR){

				for (int i =0; i<2; i++){
					this.generatePassengers(departureTime);
					if (!queuePassengers.isEmpty()){
					passIn += Math.min(queuePassengers.size()-passIn, 420-numPassengers+passOut-passIn);
					}
					departureTime += dwellTime(passIn, currOut);
					currOut =0;
				}

				if (tram.getLocation() == 11 || tram.getLocation() ==1) departureTime -= dwellTime(0, 0);			
							

				if (timeSlot<4 || (timeSlot>11 && timeSlot <40) || timeSlot > 47){
					//if ((tram.scheduledDeparture()-departureTime)>100) System.out.println("TRAM: "+tram.id+", stop:"+id+", time: "+timeEvent+", schedule: "+tram.scheduledDep[1]);
				 	departureTime = Math.max(departureTime, tram.scheduledDeparture());
				}

				if (queuePassengers.isEmpty()){
				stats.adjStats(passIn, passOut, 0, 0, departureTime - tram.scheduledDeparture(), departureTime);
				}
				else stats.adjStats(passIn, passOut, queuePassengers.size(), timeEvent - queuePassengers.peek(), departureTime - tram.scheduledDeparture(), departureTime);			

			}
			else {departureTime += new GammaDistribution(2, 0.4*(12.5+0.13*passOut)).sample()/60;}
			tram.addPassengers(passIn-passOut);

			for (int i=0;i<passIn;i++){
					avgWait+=departureTime-queuePassengers.poll();
				}


			return new Departure(departureTime, tram);
		
	}
	public Arrival planArrival(double timeEvent, Tram tram){

		double runtime = runtimeDist.sample();
		if (runtime>runtimeDist.getNumericalMean()+1.5*runtimeDist.getNumericalVariance()) return planArrival(timeEvent, tram);
		runtime = Math.max(runtime, runtimeMin);
		double arrivalTime = Math.max(timeLastArrival,timeEvent+runtime);
		timeLastArrival = arrivalTime+0.000001;
		stats.setRuntime(runtime);
		return new Arrival(arrivalTime, tram);
	}
	public boolean serverIdle(Tram tram){
		if (!this.idle){
			queueTram.addLast(tram);

			// if (queueTram.size()>2) {
			// 	out.print("----------------------------------------------------QUEUE AT STOP: "+id+" OF SIZE "+queueTram.size()+" IN QUEUE: ");
			// 	printQueue();
			// }
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
		while (currArrival<Math.min(to,945)){
			double nextArrival = getNextPassenger(currArrival);
			if (timeSlot(currArrival)==timeSlot(nextArrival) && nextArrival<to){
				currArrival = nextArrival;
				queuePassengers.add(currArrival);
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
		return Math.max(Math.min((int)Math.floor(timeEvent/15),63),0);
	}
	public void setIdle(Tram tram){
		this.idle = true;
	}

	public int[] queueSizes(){
		int[] queues = {queueTram.size(), queuePassengers.size()};
		return (queues);
	}
	public Statistics getStats(){
		return stats;
	}

	public String platform(){
		return "";
	}
	public double getAverageWait(){
		return avgWait/stats.passInTot;
	}
	// public void printQueue(){
	// 	List<Tram> temp = new ArrayList(queueTram);
	// 	for (Tram trammie : temp){
	// 		out.print(trammie.id+" ");
	// 	}
	// 	out.println("-----------------------");
	// }
}

