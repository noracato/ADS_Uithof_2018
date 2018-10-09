import org.apache.commons.math3.distribution.LogNormalDistribution;
import java.util.LinkedList; 
import java.util.Queue; 

public class Switch{
	public int id;
	public boolean idle = true;
 	LogNormalDistribution runtimeDist;
 	double runtimeMin;
	Queue<Tram> queueTram = new LinkedList<Tram>();
	public Switch (int id, double runtimeMu, double runtimeVar, double runtimeMin){
		this.id=id;
		this.runtimeMin = runtimeMin;
		this.runtimeDist  = new LogNormalDistribution(runtimeMu, runtimeVar);
	}
	public Departure planDeparture(Tram tram, double timeEvent){
			tram.setLocation(id);

			//to do: veranderen
			double switchTime = 1;
			//to do: wachten trams als ze vooruit lopen op schema?? event.tram.scheduledArr[id]),
			return new Departure(timeEvent+switchTime,tram,id);
		
	}
	public Arrival planArrival(double timeEvent, Tram tram){
		if (!this.serverIdle(tram)) return null;

		double runtime = runtimeDist.sample();
		runtime = Math.max(runtime, runtimeMin);
		//System.out.println("niet in de rij: tram "+tram.id+", time: "+timeEvent+", aankomst: "+(timeEvent+runtime));
		return new Arrival(timeEvent+runtime, tram);
	}
	public boolean serverIdle(Tram tram){
		if (!this.idle){
			//evt aanpassen
			queueTram.add(tram);
			System.out.println("in de rij: tram "+tram.id);
			return false;
		}
		else {
			this.idle = false;
			return true;
		}
	}
	public Tram nextTramInQueue(){
		return queueTram.poll();
	} 
	public void setIdle(int tramId){
		this.idle = true;
	}

}