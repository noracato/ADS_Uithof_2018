import java.util.PriorityQueue;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;


//////////// csv //////////////
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
//////////////////////////////
 

public class Main{
    static PrintStream out;
	public static void main(String[] args){

        try {writeIt("bin/out_top_test.txt");}
        catch(IOException e) {
          e.printStackTrace();
        }
        out.println("tramstop totalArriving totalLeaving totalArrMorning totalLeaveMorning totalArrEve totalLeaveEve maxQueueLength time maxWaitingTime time averageWaitTime maxTramDelay averageTramDelay/total averageTramDelay/delayed fractionOfRunsDelayed passengersNotArrived");

        // for (int q=3;q<6;q++){
        //     for (int spitsFreq=11;spitsFreq<18;spitsFreq++){
        //         for (int dayFreq=16; dayFreq<21; dayFreq++){
        //             for (int dalFreq=8; dalFreq<10; dalFreq++){
        //                 for (int it=0; it<10; it++){
        double q=3;
        int spitsFreq=12;
        int dayFreq = 7;
        int dalFreq = 6;
        for (int it=0; it<100;it++){
            UithoflijnSim simulation = new UithoflijnSim(out, q, spitsFreq, dayFreq, dalFreq);
            simulation.run();            
        }

        //                 }
        //             }
        //         }
        //     }
        // }		
	}
    private static void writeIt(String file) throws FileNotFoundException {
            out = new PrintStream(new FileOutputStream(file, true));
    }
}

class UithoflijnSim{

    PrintStream out;

	//Exercise parameters
	double time = 0;
    double q ;
    int spitsFreq;
    int dayFreq;
    int dalFreq;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(13, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops;
    PriorityQueue<double[]> schedules;
    public UithoflijnSim(PrintStream out, double q, int spitsFreq, int dayFreq, int dalFreq){
        this.out = out;
        this.q=q;
        this.spitsFreq=spitsFreq;
        this.dayFreq=dayFreq;
        this.dalFreq=dalFreq;
        this.schedules = getSchedule();
    }


    public void run(){



        tramstops = DistributionVariables.getTramStops("../input_analysis/_data/inleesbestand.csv", q);

        int print = 0;
		//to do: aanmaken trams en arrivals
		while (!eventList.isEmpty()){

            tick();
            // if(time > 60 && print == 0) {accTotPass(); print++;}
            // else if(time > 180 && print == 1) {accTotPass(); print++;}
            // else if(time > 600 && print == 2) {accTotPass(); print++;}
            // else if(time > 720 && print == 3) {accTotPass(); print++;}
		} //accTotPass(); 
        for (TramStop tramstop : tramstops){
        Statistics stats = tramstop.getStats();
        if (!tramstop.queuePassengers.isEmpty()) out.println(tramstop.id+" "+stats.totPassengers+" "+stats.totLeaving+
                " "+stats.morningPassengers+" "+stats.morningLeaving+" "+stats.eveningPassengers+" "+ stats.eveningLeaving+
                " "+stats.maxQueuePassenger+" "+stats.timeMaxPassQueue+" "+stats.maxWaitingTime+
                " "+stats.timeMaxWait+" "+tramstop.getAverageWait()+" "+stats.maxDelaytime+
                " "+stats.getAverageDelayTimeTot()+" "+stats.getAverageDelayTimeDel()+
                " "+stats.getFractionDelayedRuns()+" "+tramstop.queuePassengers.size());
        else out.println(tramstop.id+" "+stats.totPassengers+" "+stats.totLeaving+
                " "+stats.morningPassengers+" "+stats.morningLeaving+" "+stats.eveningPassengers+" "+ stats.eveningLeaving+
                " "+stats.maxQueuePassenger+" "+stats.timeMaxPassQueue+" "+stats.maxWaitingTime+
                " "+stats.timeMaxWait+" "+tramstop.getAverageWait()+" "+stats.maxDelaytime+
                " "+stats.getAverageDelayTimeTot()+" "+stats.getAverageDelayTimeDel()+
                " "+stats.getFractionDelayedRuns()+" "+0);
        }
    }

	private void tick(){
		Event nextEvent = eventList.poll();
        time = nextEvent.timeEvent;
        Tram tram = nextEvent.tram;
        
        if (nextEvent instanceof ArrivalUithof) {
            double[] nextSched = ((ArrivalUithof)nextEvent).getSchedule();
            Tram newTram = new Tram((int)nextSched[1],nextSched, ((ArrivalUithof)nextEvent).numRounds);
            //out.println("NEW TRAM: "+newTram.id+", arrival at: 0, numRounds: "+newTram.roundsLeft);
            eventList.add(new Arrival(nextSched[1]-5,newTram)); // maar is hier wel plaats voor tram?
        }
		else if (nextEvent instanceof Departure){

            // printState();

            int id = nextEvent.getLocation();
            //out.println("TRAM: "+tram.id+", departure at: "+id+" , time: "+time+" ,passengers: "+tram.getNumPassengers()+", left in queue: "+tramstops[id].queueSizes()[1]);
            //if (tramstops[id].queueSizes()[0]>1) out.println("----------------------------------------------------QUEUE AT STOP: "+id+" OF SIZE "+tramstops[id].queueSizes()[0]+"-----------------------------------------------------------------------");
            if (id==1 && tram.waitingAtPR) {
                tram.setLocation();
                //out.println("to rangeerterrein: "+tram.id); 

            }
            tramstops[id].setIdle(tram);
            if (!(id==1 && tram.waitingAtPR)) {
                Arrival nextArrival = tramstops[(id+1) % 20].planArrival(time, tram);
                eventList.add(nextArrival);                
            }            

            Tram nextTram = tramstops[id % 20].nextTramInQueue();
            if (nextTram!=null){
                //out.println("TRAM: "+nextTram.id+" DELAYED arrival at: "+(nextTram.getLocation()+1)+" , time: "+time+" ,passengers: "+nextTram.getNumPassengers());
                this.departureEvent(nextTram);
            }
			
		}
		else {//then nextEvent is Arrival
            int id = nextEvent.getLocation();

            //out.println("TRAM: "+tram.id+", arrival at: "+(id+1)+" , time: "+time+" ,passengers: "+nextEvent.tram.getNumPassengers());
            this.departureEvent(tram);
		}

	}

    // private void accTotPass(){
    //     System.out.println();
    //     System.out.println("-----------"+time+"-----------");
    //     for (TramStop tramstop : tramstops){
    //         Statistics stats = tramstop.getStats();
    //             out.println("tramstop: "+tramstop.id+", total arriving: "+stats.totPassengers+", total leaving: "+stats.totLeaving+
    //             ", maxQueueLength: "+stats.maxQueuePassenger+", at time "+stats.timeMaxPassQueue+", maxWaitingTime: "+stats.maxWaitingTime+
    //             ", at time: "+stats.timeMaxWait+", average tram delay: "+stats.getAverageDelayTime()+", fraction of runs delayed: "+stats.getFractionDelayedRuns());

    //     }
    
    private void departureEvent(Tram tram){
            //if(id==0 || id ==10) out.println(tramstops[id].platform());
            int loc = tram.getLocation();
            if (loc==0 && tram.waitingAtPR) {
                if (schedules.peek()!=null && tram.roundsLeft>0 && time<940){
                    tram.setNewSchedule(schedules.poll());
                    //out.println("new Schedule to depart at: "+tram.scheduledDep[1]+"back at: "+tram.scheduledDep[0]);
                }
            }
            Departure departure = tramstops[(loc+1) %20].planDeparture(tram,time);
            //out.println("VERTRAGING: "+tramstops[(loc+1) %20].vertraging);

            //if (departure!=null) out.println("departure planned at: "+departure.timeEvent);
            //if (departure != null && departure.getTime() > tram.scheduledDeparture()) out.println("VERTRAGING: "+(departure.getTime() - tram.scheduledDeparture())+" minuten");
            //if (tram.waitingAtPR && tram.getLocation()==1) System.out.println("Waiting at P&R at time "+time+", tram "+tram.id);
            if (departure!=null) eventList.add(departure);
    }
    private PriorityQueue<double[]> getSchedule(){
        LinkedList<double[]> schedules = DistributionVariables.schedule(q, spitsFreq, dayFreq, dalFreq, time, 930.0);
        PriorityQueue<double[]> newSchedule = new PriorityQueue<double[]>(13, (a,b) -> (int)Math.signum(a[1] - b[1]));

        // tram 58 rijdt niet weg!
        while (!schedules.isEmpty()){
            double[] nextSched = schedules.poll();
            if (newSchedule.contains(nextSched)) continue;
            //System.out.println("planned departure at: "+nextSched[1]);
            //System.out.print("NEW tram departure at: "+nextSched[1]);

            double returntime = nextSched[0];
            int i=0;
            for (double[] schedule : schedules){
                if (newSchedule.contains(schedule)) continue;
                if ((nextSched[1]+17+q>60 && returntime>180) || (nextSched[1]+17+q>600 && returntime>720)) break;
                if (returntime+q+4 <= schedule[1]){//schedule[1]-returntime < 20){ //if more than 20 min waiting - go to marshalling yard
                    //System.out.println("tram departure at: "+schedule[1]);
                    newSchedule.add(schedule);
                    returntime = schedule[0];
                    i++;
                }
                //if (i == maxRounds) break;
            }
            //System.out.println(" rounds: "+i);
            if (nextSched[1]<43-q || (nextSched[1]>180 && nextSched[1]<583-q) || nextSched[1]>720) i++;

            eventList.add(new ArrivalUithof(nextSched[1]-5, nextSched,i));
         
        }
        return newSchedule;
    }


    // private void printState(){
    //     System.out.println("time = "+time);
    //     System.out.format("%15s|%15s|%15s|%15s|\n", "StopID", "Idle", "Q Tram", "Q pass");
    //     for (TramStop tramstop : tramstops){
    //         int[] queue = tramstop.queueSizes();
    //         System.out.format("%15d|%15s|%15d|%15d|\n", tramstop.id, tramstop.idle, queue[0], queue[1]);
    //     }
    //     System.out.println();
    // }


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
    public static LinkedList<double[]> schedule(double q, int spitsFreq, int dayFreq, int dalFreq, double from, double to){
        

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
        LinkedList<double[]> arrivaltimes = new LinkedList<double[]>();
       
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
            if(time < 60-17) time += dalBetweenTime; // dus hij begint nu zo vroeg met eerder schedulen dat tram 0 al bij het eerste rondje standaard vertraging opbouwt! De betweentimes zijn optimaal gemaakt voor 60 min schedules. nu 60-st!
            else if (time < 180) time += spitsBetweenTime;
            else if (time < 600-17) time += dayBetweenTime;
            else if (time < 720) time += spitsBetweenTime;
            else time += dalBetweenTime;
        }

        return arrivaltimes;
    }

}
