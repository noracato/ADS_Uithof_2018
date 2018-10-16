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
	double time = 60;
    double q = 5;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(13, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = DistributionVariables.getTramStops("../input_analysis/_data/inleesbestand_punt.csv", q);
	// Arrival[] arrivals = DistributionVariables.getTrams("../input_analysis/_data/tramschedule_punt.csv");
    Queue<double[]> schedules = DistributionVariables.schedule(q, 15, 4, 4, time, 180.0);

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
        // Tram tram1 = new Tram(1, schedules.poll());
        // tram1.setLocation();
        // ArrivalUithof firstArr = new ArrivalUithof(time);
        // eventList.add(firstArr);

        double[] nextSched = schedules.poll();
        while (nextSched!=null){
            eventList.add(new ArrivalUithof(nextSched[1]-5, nextSched));
            nextSched = schedules.poll();
        }



		//to do: aanmaken trams en arrivals
		while (time<180){
			tick();
		}
        for (TramStop tramstop : tramstops){
            System.out.println("tramstop: "+tramstop.id+", total arriving: "+tramstop.totPassengers+", total leaving: "+tramstop.totLeaving+
                " max. queueLength: "+tramstop.maxQueueLength+", maxwaiting time: "+tramstop.maxWaitingTime);
        }
	}
	private void tick(){
		Event nextEvent = eventList.poll();
        time = nextEvent.timeEvent;
        Tram tram = nextEvent.tram;
        
        if (nextEvent instanceof ArrivalUithof) {
            Arrival newArrival = ((Eindhalte)tramstops[1]).newArrival(((ArrivalUithof)nextEvent).getSchedule());      
            eventList.add(newArrival);
        }
		else if (nextEvent instanceof Departure){
            int id = nextEvent.getLocation();
            System.out.println("TRAM: "+tram.id+", departure at: "+id+" , time: "+time+" ,passengers: "+tram.getNumPassengers());
            tramstops[id].setIdle(tram);
            Arrival nextArrival = tramstops[(id+1) % 20].planArrival(time, tram);
            eventList.add(nextArrival);
            
            

            Tram nextTram = tramstops[id % 20].nextTramInQueue();
            if (nextTram!=null){
                System.out.println("TRAM: "+nextTram.id+" DELAYED arrival at: "+(nextTram.getLocation()+1)+" , time: "+time+" ,passengers: "+nextTram.getNumPassengers());
                Departure departure = tramstops[id % 20].planDeparture(nextTram,time);
                if (!(id==1 && tram.waitingAtPR)) eventList.add(departure);
            }
			
		}
		else {//then nextEvent is Arrival
            int id = nextEvent.getLocation();
            System.out.println("TRAM: "+tram.id+", arrival at: "+(id+1)+" , time: "+time+" ,passengers: "+nextEvent.tram.getNumPassengers());
			Departure departure = tramstops[(id+1) %20].planDeparture(tram,time);
            if (tram.waitingAtPR && tram.getLocation()==1) System.out.println("Waiting at P&R at time "+time+", tram "+tram.id);
            if (departure!=null && !(tram.getLocation()==1 && tram.waitingAtPR)) eventList.add(departure);

		}

	}



}

class DistributionVariables{


	public static TramStop[] getTramStops(String fileName, double q) {
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
                    Eindhalte eindhalte = new Eindhalte(n,lambdaArr,probDep,muRuntime, varRuntime, minRuntime, q);
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
    public static Queue<double[]> schedule(double q, int spitsFreq, int dayFreq, int dalFreq, double from, double to){
        

        // calc de gewenste between time
        double roundTrip = 34+2*q;
        double st = 17+q;
        double spitsBetweenTime = 60/spitsFreq;
        double dayBetweenTime = 60/dayFreq;
        double dalBetweenTime = 60/dalFreq;

        double dalExp = Math.ceil(roundTrip/dalBetweenTime);
        dalBetweenTime -= (dalExp*dalBetweenTime - roundTrip)/dalExp;
        double dayExp = Math.ceil(roundTrip/dayBetweenTime);
        dayBetweenTime -= (dayExp*dayBetweenTime - roundTrip)/dayExp;
        double spitsExp = Math.ceil(roundTrip/spitsBetweenTime);
        spitsBetweenTime -= (spitsExp*spitsBetweenTime - roundTrip)/spitsExp;
        

        // Een queue met op elke index-tijd een mini schedule voor de tram (p+r en cs)
        Queue<double[]> arrivaltimes = new LinkedList<double[]>();
       
        // eerste daluren, dit is nog erg onhandig.....
        double time=from;
        double[] scheduledDepStops = {17+st, 0, 0, 2.1521, 3.771, 5.456, 6.775, 8.7604, 10.0625, 14.4313
                                    , 17, st, st, st+2.5375, st+6.8917, st+8.1792, st+10.1667, st+11.4708, st+13.2083333333333,st+14.8125}; 

        while(time<to){
            double[] mytimes = new double[20];
            for (int i=0; i<20;i++){
                mytimes[i]=time+scheduledDepStops[i];
            }
            arrivaltimes.add(mytimes);
            if(time < 60) time += dalBetweenTime;
            else if (time < 180) time += spitsBetweenTime;
            else if (time < 600) time += dayBetweenTime;
            else if (time < 720) time += spitsBetweenTime;
            else time += dalBetweenTime;
        }

        //System.out.println(arrivaltimes.peek()[0]);

        return arrivaltimes;
    }

}
