class Statistics{
 	public int totPassengers = 0;
 	public int totLeaving = 0;
 	public int morningPassengers = 0;
 	public int morningLeaving = 0;
 	public int eveningPassengers = 0;
 	public int eveningLeaving = 0;
 	public int currqueueSize = 0;
 	public double maxWaitingTime=0;
 	public int maxQueuePassenger=0;
 	public double timeMaxPassQueue=0;
 	public double timeMaxWait=0;
 	public double passInTot =0;
 	//delays
 	public double averageDelay=0;
 	public double maxDelay=0;
 	public double maxDelaytime=0;
 	public double totDelay=0;
 	public double numDelays=0;
 	public double numDelaysOverOne = 0;
 	public double totRuns=0;
 	public double runtime = 0;
 	public double numRuns = 0;


 	public Statistics(){}
	public void adjStats(int passIn, int passOut, int queuePassengerSize, double maxWaitingTime, double delay, double time){
		this.totPassengers += queuePassengerSize-currqueueSize;
		this.currqueueSize = queuePassengerSize-passIn;
		this.totLeaving += passOut;
		this.passInTot += passIn;
		if (time>60-17 && time<180){
			this.morningPassengers+=queuePassengerSize-currqueueSize;
			this.morningLeaving += passOut;
		}
		if (time>600-17 && time<720){
			this.eveningPassengers += queuePassengerSize-currqueueSize;
			this.eveningLeaving += passOut;
		}
		if (this.maxQueuePassenger < queuePassengerSize){
			this.maxQueuePassenger = queuePassengerSize;
			this.timeMaxPassQueue = time;
		}
		if (this.maxWaitingTime < maxWaitingTime){
			this.maxWaitingTime = maxWaitingTime;
			timeMaxWait = time;
		}
		// only measure delay at endstations!!
		if (delay>1){
			this.totDelay += delay;
			if (this.maxDelay < delay){
				this.maxDelay = delay;
				this.maxDelaytime = time;
			}
			numDelays++;
		}
		// and measure delays over one minute

		totRuns++;
	}	
	public double getAverageDelayTimeDel(){
		return totDelay/numDelays;
	}
	public double getAverageDelayTimeTot(){
		return totDelay/totRuns;
	}
	public double getFractionDelayedRuns(){
		return numDelays/totRuns;
	}
	public void setRuntime(double runtime){
		this.runtime += runtime;
		this.numRuns++;
	}
	public double getAverageRuntime(){
		return runtime/numRuns;
	}

}