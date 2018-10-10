public class Eindhalte extends TramStop{
	// 0 is 'goed' spoor, 1 is 'slecht' spoor
	private Tram[] platform = new Tram[2];
	private int q;
	// extra argument q turnaround time
	public Eindhalte (int id, double[] lambdaArr, double[] probDep, double runtimeMu, double runtimeVar, double runtimeMin, int q){
		super(id,lambdaArr,probDep,runtimeMu,runtimeVar,runtimeMin);
		this.q=q;
	}

	//when going onto platform
	@Override
	public Departure planDeparture(Tram tram, double timeEvent){


		if (tram.getLocation()==id){//then tram departure on platform
			return super.planDeparture(tram, timeEvent);
		}

		//plan departure from switch
		tram.setLocation();
		// if crossing over switch, set switch to false (else tram is going straight and nothing happens)
		if ((tram.getLocation()==id+2 && platform[1]!= null && platform[1].id == tram.id) 
				|| (tram.getLocation()==id) && platform[0]!=null && platform[0].id == tram.id){
			this.idle=false;
		}
		return new Departure(timeEvent+1,tram,id);
	}
	@Override
	public Arrival planArrival(double timeEvent, Tram tram){
		if (tram.getLocation()==(id-1)%16){//then new tram arrival
			return super.planArrival(timeEvent-1, tram);
		}
		if (tram.getLocation()==id+1 || this.idle){
			return new Arrival(timeEvent,tram);
		}
		queueTram.addFirst(tram);
		System.out.println("vooraan in de rij: tram "+tram.id);
		return null;		
	}

	//when going onto platform
	@Override
	public boolean serverIdle(Tram tram){
		for (int i=0;i<2;i++){
			if (platform[i]==null ) {
				System.out.println("reserved: platform "+i+", tramID: "+tram.id);
				platform[i]=tram;
				if (this.idle) return true;
				break;			
			}
		}
				

		queueTram.addLast(tram);
		System.out.println("in de rij: tram "+tram.id);
		return false;		
	}


	// when going off platform
	@Override
	public void setIdle(Tram tram){
		if (tram.getLocation()==id+2) {
			if (platform[0]==tram) platform[0]=null;
			else platform[1]=null;
		}
		if (tram.getLocation()!=id+1) this.idle=true;
	}
	@Override
	public double dwellTime(int passIn, int passOut){
		return q;
	}
}
