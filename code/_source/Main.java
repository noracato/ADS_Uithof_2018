import java.util.PriorityQueue;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


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
	double time = 0;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(13, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = DistributionVariables.getTramStops("../input_analysis/_data/inleesbestand_punt.csv");
	// Arrival[] arrivals = DistributionVariables.getTrams("../input_analysis/_data/tramschedule_punt.csv");
    Queue<double[]> schedules = DistributionVariables.schedule(5, 8, 4);

	public void run(){
        // Tram tram1 = new Tram(1, arrivals[0].tram.scheduledDep);
        // eventList.add(new Arrival(time,tram1));
        // Tram tram2 = new Tram(2, arrivals[1].tram.scheduledDep);
        // eventList.add(new Arrival(time+1,tram2));
        // Tram tram3 = new Tram(3, arrivals[2].tram.scheduledDep);
        // eventList.add(new Arrival(time+2,tram3));
        // for (Arrival arrival : arrivals){
        //     eventList.add(arrival);
        // }

        // die get moet misschien een queue worden
        double[] newSchedule = schedules.poll();
        Tram tram1 = new Tram(1, newSchedule);
        eventList.add(new Arrival(time,tram1));


		//to do: aanmaken trams en arrivals
		while (time<30){
			tick();
		}
        for (TramStop tramstop : tramstops){
            System.out.println("tramstop: "+tramstop.id+", max waitingtime: "+tramstop.totPassengers);
        }
	}
	private void tick(){
		Event nextEvent = eventList.poll();
        int id = nextEvent.getLocation();
        time = nextEvent.timeEvent;
        Tram tram = nextEvent.tram;

		if (nextEvent instanceof Departure){
            System.out.println("TRAM: "+tram.id+", departure at: "+id+" , time: "+time+" ,passengers: "+tram.getNumPassengers());
            tramstops[id].setIdle(tram);
            Arrival nextArrival = tramstops[(id+1) % 20].planArrival(time, tram);
            eventList.add(nextArrival);
            
            

            Tram nextTram = tramstops[id % 20].nextTramInQueue();
            if (nextTram!=null){
                System.out.println("TRAM: "+nextTram.id+" DELAYED arrival at: "+(nextTram.getLocation()+1)+" , time: "+time+" ,passengers: "+nextTram.getNumPassengers());
                eventList.add(tramstops[id % 20].planDeparture(nextTram,time));
            }
			
		}
		else {
            System.out.println("TRAM: "+tram.id+", arrival at: "+(id+1)+" , time: "+time+" ,passengers: "+nextEvent.tram.getNumPassengers());
			Departure departure = tramstops[(id+1) %20].planDeparture(tram,time);
            if (departure!=null) eventList.add(departure);

		}

	}



}

class DistributionVariables{


	public static TramStop[] getTramStops(String fileName) {
		TramStop[] tramstops = new TramStop[20];
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

                double[] lambdaArr =new double[64];
                double[] probDep =new double[64];

                for (int i=0;i<64;i++){lambdaArr[i] = Double.parseDouble(timeslotN[i+1])/15;}
                for (int i=0;i<64;i++){probDep[i] = Double.parseDouble(timeslotN[i+65]);}
                double muRuntime = Double.parseDouble(timeslotN[129]);
                double varRuntime = Double.parseDouble(timeslotN[130]);
                double minRuntime = Double.parseDouble(timeslotN[131]);

                if (n==0 || n==10){
                    Eindhalte eindhalte = new Eindhalte(n,lambdaArr,probDep,muRuntime, varRuntime, minRuntime, 5);
                    tramstops[n] = eindhalte;
                    tramstops[n+1] = eindhalte;
                    tramstops[n+2] = eindhalte;
                    n=n+2;
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
    // public static Arrival[] getTrams(String fileName) {
    //     // dit niet zelf generen op basis van q, spitsFreq en dalFreq?
    //     Arrival[] scheduledArr = new Arrival[12];
    //     String csvFile = fileName;
    //     BufferedReader br = null;
    //     String line = "";
    //     String csvSplitBy = ";";    

    //     try {

    //         br = new BufferedReader(new FileReader(csvFile));

            
    //         int n = 0;
    //         String[] departures;

    //         while ((line = br.readLine()) != null) {

    //             departures = line.split(csvSplitBy);

    //             double[] scheduledDep =new double[19];

    //             for (int i=0;i<departures.length;i++){scheduledDep[i] = Double.parseDouble(departures[i]);}
    //             scheduledArr[n]= new Arrival(scheduledDep[0], new Tram(n, scheduledDep));
    //             n++;
    //         }

    //     } catch (FileNotFoundException e) {
    //         e.printStackTrace();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     } finally {
    //         if (br != null) {
    //             try {
    //                 br.close();
    //             } catch (IOException e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     }
    //     return scheduledArr;        
    // }

    // freq is trams per hour
    public static Queue<double[]> schedule(int q, int spitsFreq, int dalFreq){
        // int roundTrip = 34+2*q; // dit is dus die met alle stops

        // calc de gewenste between time
        double roundTrip = 27+2*q;
        double singleTrip = 13.5+q;
        double spitsBetweenTime = 60/spitsFreq;
        double dalBetweenTime = 60/dalFreq;

        // kijk hoeveel trams je daarvoor moet inzetten
        int dalTrams = (int)Math.ceil(roundTrip/dalBetweenTime);
        int spitsTrams = (int)Math.ceil(roundTrip/spitsBetweenTime);
        int extraTrams = spitsTrams-dalTrams;

        // herbereken de between time op basis van aantal trams voor mooie spreiding
        dalBetweenTime = 60/dalTrams;
        spitsBetweenTime = 60/spitsTrams;

        // Een queue met op elke index-tijd een mini schedule voor de tram (p+r en cs)
        Queue<double[]> arrivaltimes = new LinkedList<double[]>();
       
        // eerste daluren, dit is nog erg onhandig.....
        double time=0;
        // for(int i = 0; i < dalTrams; i++){
            // time = dalBetweenTime*i;      
            while(time<60){
                double[] mytimes = new double[]{time, time+singleTrip};
                arrivaltimes.add(mytimes);
                time += roundTrip;
            }
        // }
        System.out.println(arrivaltimes.peek());


        System.out.println(spitsBetweenTime +" "+dalBetweenTime+" "+dalTrams+" "+spitsTrams);
        return arrivaltimes;
    }

}
