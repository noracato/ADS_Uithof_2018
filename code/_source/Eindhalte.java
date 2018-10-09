public class Eindhalte extends TramStop{
	// 0 is 'goed' spoor, 1 is 'slecht' spoor
	private Tram[] platform = new Tram[2];
	private boolean switchIdle = true;
	// private Switch tramSwitch = new Switch();
	private int q = 5;
	// extra argument q turnaround time
	public Eindhalte (int id, double[] lambdaArr, double[] probDep, double runtimeMu, double runtimeVar, double runtimeMin, int q){
		super(id,lambdaArr,probDep,runtimeMu,runtimeVar,runtimeMin);
	}

	//when going onto platform
	@Override
	public boolean serverIdle(Tram tram){
		for (int i=0;i<2;i++){
			if (platform[i]==null ) { 
				platform[i]=tram;
				return true; 			
			}
		}
		queueTram.add(tram);
		System.out.println("in de rij: tram "+tram.id);
		return false;		
	}

	// when going off platform
	@Override
	public void setIdle(int tramId){
		if (platform[0]!=null && platform[0].id==tramId){ 
			platform[0]=null;
		}
		else { 
			platform[1] = null;
		}
	}

	@Override
	public double dwellTime(int passIn, int passOut){
		return q;
	}

	// check if on switch
	@Override
	public double waitForDeparture(Tram tram){
		double extra = 0.0;
		// check if tram is on 'bad' platform
		if (platform[1]!=null && platform[1].id==tramId){
			if(!switchIdle){extra = 1.0;}
			// te vroeg?
			else{switchIdle = false;}
		}
		return extra;
	}

	// to do: hoe weten we om welke tijd hij hier is? een lijst bijhouden voor op welke tijden hij bezet is?
	// en wie heeft er voorrang?
	@Override
	public double waitForArrival(Tram tram){
		double extra = 0.0;
		// check if tram is on 'bad' platform
		if (platform[0]!=null && platform[0].id==tramId){
			if(!switchIdle){extra = 1.0;}
		}
		return extra;
	}
}
