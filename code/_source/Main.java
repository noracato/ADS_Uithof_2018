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
import java.io.PrintWriter ;
import java.io.IOException;
import java.io.File;
//////////////////////////////
 

public class Main{
	public static void main(String[] args){
		UithoflijnSim simulation = new UithoflijnSim();
		simulation.run();
		
	}
}

class UithoflijnSim{
	//Exercise parameters
	double time = 0;
    double q = 5;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(13, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = DistributionVariables.getTramStops("../input_analysis/_data/inleesbestand_punt.csv", q);
	// Arrival[] arrivals = DistributionVariables.getTrams("../input_analysis/_data/tramschedule_punt.csv");

    Queue<double[]> schedules = DistributionVariables.schedule(q, 15, 4, 4, time, 930.0);

    public void run(){

        double[] nextSched = schedules.poll();
        while (nextSched!=null){
            System.out.println("planned departure at: "+nextSched[1]);
            eventList.add(new ArrivalUithof(nextSched[1]-5, nextSched));
            nextSched = schedules.poll();
        }

        int print = 0;
		//to do: aanmaken trams en arrivals
		while (!eventList.isEmpty()){

            tick();
            if(time > 60 && print == 0) {accTotPass(); print++;}
            else if(time > 180 && print == 1) {accTotPass(); print++;}
            else if(time > 600 && print == 2) {accTotPass(); print++;}
            else if(time > 720 && print == 3) {accTotPass(); print++;}
		} accTotPass();
	}

    private void accTotPass(){
        System.out.println();
        System.out.println("-----------"+time+"-----------");
        for (TramStop tramstop : tramstops){
            System.out.println("tramstop: "+tramstop.id+//", total arriving: "+tramstop.totPassengers+", total leaving: "+tramstop.totLeaving+
                " max. queueLength: "+tramstop.maxQueueLength+", at time "+tramstop.timeMaxQueue+", maxwaiting time: "+tramstop.maxWaitingTime+
                ", at time: "+tramstop.timeMaxWait);
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
            printState();
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

    private void printState(){
        System.out.println("time = "+time);
        System.out.format("%15s|%15s|%15s|%15s|\n", "StopID", "Idle", "Q Tram", "Q pass");
        for (TramStop tramstop : tramstops){
            int[] queue = tramstop.queueSizes();
            System.out.format("%15d|%15s|%15d|%15d|\n", tramstop.id, tramstop.idle, queue[0], queue[1]);
        }
        System.out.println();
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

        return tramstops;
    }
   
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
            if(time +dalBetweenTime < 60-st) time += dalBetweenTime;
            else if (time + spitsBetweenTime < 180) time += spitsBetweenTime;
            else if (time + dayBetweenTime < 600-st) time += dayBetweenTime;
            else if (time + spitsBetweenTime < 720) time += spitsBetweenTime;
            else time += dalBetweenTime;
        }

        return arrivaltimes;
    }

}
