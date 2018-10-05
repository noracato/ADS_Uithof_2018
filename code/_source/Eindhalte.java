public class Eindhalte extends TramStop{
	private Tram[] platform = new Tram[2];
	public Eindhalte (int id, double[] lambdaArr, double[] probDep, double runtimeMu, double runtimeVar, double runtimeMin){
		super(id,lambdaArr,probDep,runtimeMu,runtimeVar,runtimeMin);
	}
	@Override
	public Arrival planArrival(double timeEvent, Tram tram){
		int idleServer = this.isIdle();
		if (idleServer<0){
			queueTram.add(tram);
			System.out.println("in de rij: tram "+tram.id);
			return null;
		}
		else this.platform[idleServer] = tram;
		// to do: geen trams aankomen tijdens switch

		double runtime = runtimeDist.sample();
		runtime = Math.max(runtime, runtimeMin);
		System.out.println("niet in de rij: tram "+tram.id+", time: "+timeEvent+", aankomst: "+(timeEvent+runtime));

		return new Arrival(timeEvent+runtime, tram);
	}
	private int isIdle(){
		if (platform[0]==null ) return 0;
		else if (platform[1]==null){ return 1;}
		else {return -1;}
	}
	@Override
	public void setIdle(int tramId){
		if (platform[0]!=null && platform[0].id==tramId){ platform[0]=null;}
		else {platform[1]=null;}
	}
}