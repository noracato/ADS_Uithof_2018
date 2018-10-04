import java.util.PriorityQueue;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Arrays; 


//////////// csv //////////////
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//////////////////////////////
 

public class Main{
	public static void main(String[] args){
		UithoflijnSim simulation = new UithoflijnSim();
		simulation.run();
		// simulation.run();
		
	}
}

class UithoflijnSim{
	//Exercise parameters
	double time = 6.05;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(13, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = DistributionVariables.getTramStops("../../input_analysis/_data/inleesbestand_punt.csv");
	

	public void run(){
        Tram tram1 = new Tram();
        eventList.add(new Arrival(time,tram1));
		//to do: aanmaken trams en arrivals
		while (time<1000){
			tick();
		}
	}
	private void tick(){
		Event nextEvent = eventList.poll();
        time = nextEvent.timeEvent;
        TramStop currTramstop = tramstops[nextEvent.getLocation()];


		if (nextEvent instanceof Departure){
            System.out.println("departure at: "+currTramstop.id+" , time: "+time+" ,passengers: "+nextEvent.tram.getNumPassengers());
			eventList.add(currTramstop.planArrival(time, nextEvent.tram));

            Event nextTram = currTramstop.nextTramInQueue();
            if (nextTram!=null){
                System.out.println("DELAYED arrival at: "+currTramstop.id+" , time: "+time+" ,passengers: "+nextEvent.tram.getNumPassengers());
                eventList.add(currTramstop.planDeparture(nextTram,time+(double)2/3));
            }
			
		}
		else {
            //to do: is dit altijd 40sec+ na departure vorige tram
            System.out.println("arrival at: "+currTramstop.id+" , time: "+time+" ,passengers: "+nextEvent.tram.getNumPassengers());
			Departure departure = currTramstop.planDeparture(nextEvent,time);
            eventList.add(departure);

		}

	}



}

class DistributionVariables{	

	public static TramStop[] getTramStops(String fileName) {
		TramStop[] tramstops = new TramStop[12];
        String csvFile = fileName;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(csvFile));

            String firstLine = br.readLine();

            String[] timeslotN;
            int n = 0;

            while ((line = br.readLine()) != null) {

                timeslotN = line.split(csvSplitBy);

                // do something with reversed order
                double[] lambdaArr =new double[64];
                double[] probDep =new double[64];

                for (int i=0;i<64;i++){lambdaArr[i] = 4 *Double.parseDouble(timeslotN[i+1]);}
                for (int i=0;i<64;i++){probDep[i] = Double.parseDouble(timeslotN[i+65]);}
                //to do mu's importeren
                tramstops[n] = new TramStop(n,lambdaArr,probDep,0.03);
              	n++;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // System.out.println("tramid: "+tramstops[2].id);
        // for (int i = 0; i<64; i++){
        // 	System.out.println("lambdaIn: "+ tramstops[2].lambdaArr[i]+" , lambdaDep: "+tramstops[2].lambdaDep[i]);
        // }
        return tramstops;
    }
}
