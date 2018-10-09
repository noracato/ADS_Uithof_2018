public class Eindhalte extends TramStop{
	// 0 is 'goed' spoor, 1 is 'slecht' spoor
	private Tram[] platform = new Tram[2];
	private Tram switchIdle;
	private Switch tramSwitch;
	private int q;
	// extra argument q turnaround time
	public Eindhalte (int id, double runtimeMu, double runtimeVar, double runtimeMin, double[] lambdaArr, double[] probDep, int q){
		super(id,runtimeMu,runtimeVar,runtimeMin,lambdaArr,probDep);
		this.q=q;
		tramSwitch = new Switch(id,runtimeMu,runtimeVar,runtimeMin);
	}
	@Override
	public Departure planDeparture(Tram tram, double timeEvent){
		//return tramSwitch.planDeparture();
	}
	@Override
	public Arrival planArrival(double timeEvent, Tram tram){
		
	}

	//when going onto platform
	@Override
	public boolean serverIdle(Tram tram){
		if (switchIdle==null && platform[0]==null){
			switchIdle=tram;
			return true;
		}
		else if (platform[1]==null){
			platform[1]=tram;
			return true;	
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

}
