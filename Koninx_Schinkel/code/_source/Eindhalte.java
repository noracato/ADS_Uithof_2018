
public class Eindhalte extends TramStop{
	// 0 is 'goed' spoor, 1 is 'slecht' spoor
	private Tram[] platform = new Tram[2];
	private Tram idle;
	private double q;
	private int numTram = 0;
	// extra argument q turnaround time
	public Eindhalte (int id, double[] lambdaArr, double[] probDep, double runtimeMu, double runtimeVar, double runtimeMin, double q){
		super(id,lambdaArr,probDep,runtimeMu,runtimeVar,runtimeMin);
		this.q=q;
	}

	//when going onto platform
	@Override
	public Departure planDeparture(Tram tram, double timeEvent){


		if (!this.serverIdle(tram)){
				queueTram.addLast(tram);
				return null;
		}

		if (tram.getLocation()==id){//then tram departure on platform
			if(serverIdle(tram)) return super.planDeparture(tram, timeEvent);
			else { // als het een nieuwe tram is
				queueTram.addFirst(tram);
				return null;
			}
		}
		
		//plan departure from switch
		//set switch to false
		if (this.idle==null || switchAvailable(tram)){
			this.idle=tram;
			//System.out.println("SWITCH BEZET");		
			tram.setLocation();
	
			if ( tram.getLocation()==id+2 && platform[0]==tram) return new Departure(timeEvent,tram); //instant departure
			return new Departure(timeEvent+1,tram);
		}
		//switch is occupied 	
		queueTram.addFirst(tram);
		return null;				

	}
	@Override
	public Arrival planArrival(double timeEvent, Tram tram){
		if (tram.getLocation()==(id-1)%20){//then new tram arrival
			return super.planArrival(timeEvent-1, tram);
		}
		//otherwise instant arrival
		return new Arrival(timeEvent,tram);
		
	}
	private boolean switchAvailable(Tram tram){
		if ((tram.getLocation()==(id-1)%20 && platform[1]==tram && this.idle.getLocation()==id+2 && platform[0]==this.idle) ||
			(tram.getLocation()==id+1 && platform[0]==tram && this.idle.getLocation()==id && platform[1]==this.idle)){
			return true;
		}
		return false;
	}
	//when going onto platform
	@Override
	public boolean serverIdle(Tram tram){
		if (platform[0]==tram || platform[1]==tram) return true;

		for (int i=0;i<2;i++){
			if (platform[i]==null) {
				// System.out.println("reserved: platform "+i+", tramID: "+tram.id);
				platform[i]=tram;
				return true;
			}
		}
		return false;
	}


	// when going off platform
	@Override
	public void setIdle(Tram tram){
		if (tram.getLocation()==id+2) {
			if (platform[0]==tram) platform[0]=null;
			else platform[1]=null;
		}
		if (tram.getLocation()!=id+1 && this.idle==tram) {
			this.idle=null;
		}
	}
	@Override
	public double dwellTime(int passIn, int passOut){
		return q;
	}
	@Override
	public Tram nextTramInQueue(){
		Tram tram = queueTram.peek();
		if (tram !=null && serverIdle(tram) && (this.idle==null || switchAvailable(tram))) return queueTram.poll();
		return null;
	}
	// private boolean backToStart(Tram tram){
	// 	if (tram.getLocation() !=19) return false;
	// 	for (int i =0; i<2;i++){
	// 		if (platform[i].getNumPassengers()==0 && platform[i].waitingAtPR){
	// 			//out.println("Tram "+platform[i].id+" naar rangeerterrein. Tram "+tram.id+"op P&R");
	// 			platform[i]=tram;
	// 			return true;
	// 		}			
	// 	}
	// 	return false;
	// }

	public String platform(){
		String platforms = "";
		if (platform[0]!=null) platforms += "Platform 0: "+platform[0].id+" ";
		if (platform[1]!=null) platforms += "Platform 1: "+platform[1].id;
		return (platforms);
	}


	// public Arrival newArrival(double[] schedule){
	// 	this.numTram++;
	// 	for (int i =0; i<2;i++){
	// 		if (platform[i]!=null && platform[i].getLocation()==1 && platform[i].getNumPassengers()==0 && platform[i].waitingAtPR){
	// 			//out.println("Tram "+platform[i].id+" rescheduled for new departure on P&R");
	// 			platform[i].location--;
	// 			platform[i].setNewSchedule(schedule);
	// 			super.planDeparture(platform[i],schedule[1]-5);
	// 			return new Arrival(schedule[1]-5,platform[i]);
	// 		}			
	// 	}
	// 	//out.println("NEW tram "+(int)schedule[1]+" scheduled at "+schedule[1]+" to depart on P&R");
	// 	Tram newTram = new Tram((int)schedule[1],schedule);
	// 	return new Arrival(schedule[1]-5,newTram);
	// }
}
