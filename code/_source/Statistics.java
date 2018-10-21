class Statistics{
 	public int totPassengers = 0;
 	public int totLeaving = 0;
 	public int currqueueSize = 0;
 	public double maxWaitingTime=0;
 	public int maxQueuePassenger=0;
 	public double timeMaxPassQueue=0;
 	public double timeMaxWait=0;
 	//delays
 	public double averageDelay=0;
 	public double maxDelay=0;
 	public double totDelay=0;
 	public double numDelays=0;
 	public double numDelaysOverOne = 0;
 	public double totRuns=0;


 	public Statistics(){}
	public void adjStats(int passIn, int passOut, int queuePassengerSize, double maxWaitingTime, double delay, double time){
		this.totPassengers += queuePassengerSize-currqueueSize;
		this.currqueueSize = queuePassengerSize-passIn;
		this.totLeaving += passOut;
		if (this.maxQueuePassenger < queuePassengerSize){
			this.maxQueuePassenger = queuePassengerSize;
			this.timeMaxPassQueue = time;
		}
		if (this.maxWaitingTime < maxWaitingTime){
			this.maxWaitingTime = maxWaitingTime;
			timeMaxWait = time;
		}
		// only measure delay at endstations!!
		if (delay>0){
			this.totDelay += delay;
			this.maxDelay = Math.max(maxDelay, delay);
			numDelays++;
		}
		// and measure delays over one minute
		if (delay>1){
			numDelaysOverOne ++;
		}
		totRuns++;
	}
	public double getAverageDelayTime(){
		return totDelay/totRuns;
	}
	public double getFractionDelayedRuns(){
		return numDelaysOverOne/totRuns;
	}

}