import java.util.Random;
import java.util.PriorityQueue;
import java.util.LinkedList; 
import java.util.Queue; 

public class Main{
	public static void main(String[] args){
		UithoflijnSim simulation = new UithoflijnSim();
		simulation.run();
	}
}

class UithoflijnSim{
	//Exercise parameters


	PriorityQueue<Event> eventList = new PriorityQueue<Event>(s+1, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));

	public void run(){


	}
	private void tick(){
		time = eventList.peek().timeEvent;

	}



}




