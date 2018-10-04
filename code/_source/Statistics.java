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