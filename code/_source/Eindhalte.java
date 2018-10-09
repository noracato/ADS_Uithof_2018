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

}
