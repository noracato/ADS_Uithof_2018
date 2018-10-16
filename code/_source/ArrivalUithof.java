public class ArrivalUithof extends Event{
	double[] scheduledDeps;
	public ArrivalUithof(double timeEvent, double[] scheduledDeps){
		super(timeEvent);
		this.scheduledDeps=scheduledDeps;
	}
	public double[] getSchedule(){
		return scheduledDeps;
	}
}