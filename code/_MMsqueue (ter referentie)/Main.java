import java.util.Random;
import java.util.PriorityQueue;
import java.util.LinkedList; 
import java.util.Queue; 

public class Main{
	public static void main(String[] args){
		MMsQueue mmsSim = new MMsQueue();
		mmsSim.run();
	}
}

class MMsQueue{
	//Exercise parameters
	double lambdaA = 1;
	double lambdaS = 0.25;
	int n = 0;
	int s = 5;

	double time = 0;

 	Random rand = new Random(); 
	Server[] servers = new Server[s];
	PriorityQueue<Arrival> eventList = new PriorityQueue<Arrival>(s+1, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	Queue<Arrival> queue = new LinkedList<Arrival>();
	Statistics statistics = new Statistics();
	public void run(){
		for (int i=0; i<s ; i++) {
			servers[i] = new Server(i);
		}
		double arrivalTime = getNext(lambdaA);
		eventList.add(new Arrival(arrivalTime));
		statistics.newArrival(arrivalTime);


		while (n < 1000){
			tick();
		}
		System.out.println("END OF SIMULATION");
		System.out.println("Average delay: "+statistics.getAverageDelayTime()/n);
		System.out.println("Average customers in queue: "+statistics.getTotalQueueLength()/time);
		System.out.println("Average arrival rate "+statistics.getAverageArrivalRate());

		for (int k=0; k<s;k++){
			double utilizationk = servers[k].getUtilization();
			System.out.println("Average utilization server "+k+" : "+utilizationk);

		}


	}
	private void tick(){
		time = eventList.peek().timeEvent;
		statistics.update(time, queue.size());


		if (eventList.peek() instanceof Departure){ //if event is departure
			//update system state
			int serverID = ((Departure)eventList.poll()).serverID;
			System.out.println("departure at server:"+serverID+", at time: "+ time);	
			servers[serverID].makeAvailable();			

			//generate future Events
			if (!queue.isEmpty()){
				Arrival nextCustomer = queue.poll();
				planDeparture(serverID);
				n++;
				statistics.delayedArrival(time - nextCustomer.timeEvent);
				System.out.println("DELAYED arrival at server: "+serverID+", at time: "+ time);

			}
		}
		else { //if event is arrival
			//update system state
			Arrival currArrival = eventList.poll();

			int firstIdleServer = findIdleServer(); //returns -1 is no server available
			if (firstIdleServer >=0){
				planDeparture(firstIdleServer);
				System.out.println("arrival at server: "+firstIdleServer+", at time: "+ time);
			}
			else {
				queue.add(currArrival);
				System.out.println("Queuelength: "+ queue.size());

			}
			double nextArrival = getNext(lambdaA);
			statistics.newArrival(nextArrival);
			eventList.add(new Arrival(time + nextArrival));

		}
	}
	private void planDeparture(int serverID){
		double serveTime = getNext(lambdaS);
		servers[serverID].serveCustomer(serveTime);
		eventList.add(new Departure(time + serveTime, serverID));
	}
	private double getNext(double lambda) {
    	return  Math.log(1-rand.nextDouble())/(-lambda);
	}
	private int findIdleServer(){
		for (int j=0;j<s;j++){
			if (servers[j].isIdle()) return j;
		}
		return -1;
	}
}
class Statistics{
	double totalDelayTime=0;
	double currentTime = 0;
	double totQueueLength=0;
	double totArrivals=0;
	double arrivalTimes=0;
	public void newArrival(double timeEvent){
		totArrivals++;
		arrivalTimes+= timeEvent;
	}
	public void delayedArrival(double delayedTime){
		this.totalDelayTime += delayedTime;
	}
	public double getAverageDelayTime(){
		return totalDelayTime;
	}
	public void update(double time, int queueLength){
		totQueueLength += (time-currentTime)*queueLength;
		currentTime = time;
	}
	public double getTotalQueueLength(){
		return totQueueLength;
	}
	public double getAverageArrivalRate(){
		return arrivalTimes/totArrivals;
	}
}

class Server{
	int id;
	private boolean idle = true;
	double totServeTime = 0;
	int totalServed = 0;
	public Server(int id){
		this.id = id;
	}
	public void serveCustomer(double serveTime){
		this.idle = false;
		this.totalServed++;
		this.totServeTime += serveTime;
	}
	public boolean isIdle(){
		return idle;
	}
	public void makeAvailable(){
		this.idle = true;
	}
	public double getUtilization(){
		return totServeTime/totalServed;
	}
}

class Arrival{
	double timeEvent;
	public Arrival(double timeEvent){
		this.timeEvent = timeEvent;
	}
	public double getTime(){
		return timeEvent;
	}
}

class Departure extends Arrival{
	int serverID;
	public Departure(double timeEvent, int serverID){
		super(timeEvent);
		this.serverID = serverID;
	}
}


