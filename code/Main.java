import java.util.PriorityQueue;

public class Main{
	public static void main(String[] args){
		UithoflijnSim simulation = new UithoflijnSim();
		simulation.run();
	}
}

class UithoflijnSim{
	//Exercise parameters
	double time = 0;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(s+1, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = new TramStop[12];
	Event
	public void run(){


	}
	private void tick(){
		time = eventList.peek().timeEvent;

	}



}




