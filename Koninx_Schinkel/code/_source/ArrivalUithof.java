public class ArrivalUithof extends Event{
	double[] scheduledDeps;
	public int numRounds;
	public ArrivalUithof(double timeEvent, double[] scheduledDeps, int numRounds){
		super(timeEvent);
		this.scheduledDeps=scheduledDeps;
		this.numRounds=numRounds;
	}
	public double[] getSchedule(){
		return scheduledDeps;
	}
}