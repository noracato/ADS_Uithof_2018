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
	double time = 120;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(13, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = DistributionVariables.getTramStops("../input_analysis/_data/inleesbestand_punt.csv");
	

	public void run(){
        Tram tram1 = new Tram(1);
        eventList.add(new Arrival(time,tram1));
        Tram tram2 = new Tram(2);
        eventList.add(new Arrival(time+1,tram2));

		//to do: aanmaken trams en arrivals
		while (time<960){
			tick();
		}
	}
	private void tick(){
		Event nextEvent = eventList.poll();
        int id = nextEvent.getLocation();
        time = nextEvent.timeEvent;
        Tram tram = nextEvent.tram;

		if (nextEvent instanceof Departure){
            System.out.println("TRAM: "+tram.id+", departure at: "+id+" , time: "+time+" ,passengers: "+tram.getNumPassengers());
            tramstops[id].setIdle(tram.id);
            Arrival nextArrival = tramstops[id+1].planArrival(time, tram);
            if (nextArrival!=null) {eventList.add(nextArrival);}

            Tram nextTram = tramstops[id].nextTramInQueue();
            if (nextTram!=null){
                System.out.println("TRAM: "+nextTram.id+"DELAYED arrival at: "+id+" , time: "+time+" ,passengers: "+nextTram.getNumPassengers());
                eventList.add(tramstops[id].planDeparture(nextTram,time+(double)2/3));
            }
			
		}
		else {
            System.out.println("TRAM: "+tram.id+"arrival at: "+(id+1)+" , time: "+time+" ,passengers: "+nextEvent.tram.getNumPassengers());
			Departure departure = tramstops[id+1].planDeparture(tram,time);
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

                for (int i=0;i<64;i++){lambdaArr[i] = Double.parseDouble(timeslotN[i+1])/15;}
                for (int i=0;i<64;i++){probDep[i] = Double.parseDouble(timeslotN[i+65]);}
                double muRuntime = Double.parseDouble(timeslotN[129]);
                double varRuntime = Double.parseDouble(timeslotN[130]);
                double minRuntime = Double.parseDouble(timeslotN[131]);

                //to do mu's importeren
                if (n==0 || n==6){
                    tramstops[n] = new Eindhalte(n,lambdaArr,probDep,muRuntime, varRuntime, minRuntime);
                }
                else {tramstops[n] = new TramStop(n,lambdaArr,probDep,muRuntime, varRuntime, minRuntime);}
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
