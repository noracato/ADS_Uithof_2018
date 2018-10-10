public class Eindhalte extends TramStop{
	// 0 is 'goed' spoor, 1 is 'slecht' spoor
	private Tram[] platform = new Tram[2];
	// private Switch tramSwitch = new Switch();
	private int q = 5;
	// extra argument q turnaround time
	public Eindhalte (int id, double[] lambdaArr, double[] probDep, double runtimeMu, double runtimeVar, double runtimeMin, int q){
		super(id,lambdaArr,probDep,runtimeMu,runtimeVar,runtimeMin);
	}

	//when going onto platform
	@Override
	public Departure planDeparture(Tram tram, double timeEvent){

		if (tram.getLocation()==id && (tramInPlatform(0,tram.id) || tramInPlatform(1,tram.id)) ){//then tram departure on platform
			super(tram, timeEvent);
		}

		//plan departure from switch
		this.idle=false;
		tram.setLocation(id);
		return new Departure(timeEvent+1,tram,id);
	}
	@Override
	public Arrival planArrival(double timeEvent, Tram tram){
		if (tram.getLocation()!=id){//then new tram arrival
			super(timeEvent-1, tram);
		}
		if (platform[0]==tram || platform[1]==tram || this.idle){
			return new Arrival(timeEvent,tram);
		}
		queueTram.addFirst(tram);
		return null;		
	}

	//when going onto platform
	@Override
	public boolean serverIdle(Tram tram){
		if (this.idle()){
			for (int i=0;i<2;i++){
				if (platform[i]==null ) { 
					platform[i]=tram;
					return true; 			
				}
			}
		}			

		queueTram.addLast(tram);
		System.out.println("in de rij: tram "+tram.id);
		return false;		
	}


	// when going off platform
	@Override
	public void setIdle(Tram tram){
		if (tram.getLocation()==id) {
			if (platform[0]==tram) platform[0]==null;
			else if (platform[1]==tram) platform[1]==null;
			else this.idle=true;		
		}
		else this.idle=true;
	}
	@Override
	public double dwellTime(int passIn, int passOut){
		return q;
	}
}
