public class Eindhalte extends TramStop{
	private Tram[] platform = new Tram[2];
	public Eindhalte (int id, double[] lambdaArr, double[] probDep, double runtimeMu, double runtimeVar, double runtimeMin){
		super(id,lambdaArr,probDep,runtimeMu,runtimeVar,runtimeMin);
	}
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
	@Override
	public void setIdle(int tramId){
		if (platform[0]!=null && platform[0].id==tramId){ 
			platform[0]=null;
		}
		else { 
			platform[1] = null; 
		}
	}
}