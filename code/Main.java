import java.util.PriorityQueue;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.ArrayList; 

//////////// csv //////////////
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//////////////////////////////


public class Main{
	public static void main(String[] args){
		// UithoflijnSim simulation = new UithoflijnSim();
		// simulation.run();
		DistributionVariables dist = new DistributionVariables("../input_analysis/_data/12a_progn_adjusted_timeslot_adjusted.csv");
	}
}

class UithoflijnSim{
	//Exercise parameters
	double time = 0;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(s+1, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = new TramStop[12];
	Event

	public void run(){
		System.out.println("runnnnnnnnnnning");

	}
	// private void tick(){
	// 	time = eventList.peek().timeEvent;

	// }



}

class DistributionVariables{
	// p+r de uithof
	// wkz
	ArrayList<Double> umcLambas = new ArrayList<Double>(64);
	ArrayList<Double> umcOut = new ArrayList<Double>(64);
	ArrayList<Double> heidelLambdas = new ArrayList<Double>(64);
	ArrayList<Double> heidelOut = new ArrayList<Double>(64);
	ArrayList<Double> paduaLambads = new ArrayList<Double>(64);
	ArrayList<Double> paduaOut = new ArrayList<Double>(64);
	ArrayList<Double> kromLambdas = new ArrayList<Double>(64);
	ArrayList<Double> kromOut = new ArrayList<Double>(64);
	ArrayList<Double> galgLambdas = new ArrayList<Double>(64);
	ArrayList<Double> galgOut = new ArrayList<Double>(64);
	ArrayList<Double> bleekLambdas = new ArrayList<Double>(64);
	ArrayList<Double> bleekOut = new ArrayList<Double>(64);
	ArrayList<Double> csLambdas = new ArrayList<Double>(64);
	ArrayList<Double> csout = new ArrayList<Double>(64);

	boolean orderReversed = false;

	public DistributionVariables(String fileName) {

        String csvFile = fileName;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(csvFile));

  			// skip first two lines and check for reversed
            String firstLine = br.readLine();
            firstLine = br.readLine();
            if (!firstLine.split(csvSplitBy)[1].equals("AZU")) {
            	orderReversed = true;
            }

            String[] timeslotN;
            int n = 0;

            while ((line = br.readLine()) != null) {

                timeslotN = line.split(csvSplitBy);

                // do something with reversed order

                umcLambas.add(n, Double.parseDouble(timeslotN[1]));
                heidelLambdas.add(n, Double.parseDouble(timeslotN[2]));

                double hi = umcLambas.get(0);
                double no = umcLambas.get(1);

              	System.out.println(hi);
              	System.out.println(no);
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

    }


}


