import java.util.PriorityQueue;
import java.util.LinkedList; 
import java.util.Queue;
//import java.util.ArrayList; 
import java.util.Arrays; 


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
		DistributionVariables dist = new DistributionVariables("../input_analysis/_data/inleesbestand_punt.csv");
	}
}

class UithoflijnSim{
	//Exercise parameters
	double time = 0;

	PriorityQueue<Event> eventList = new PriorityQueue<Event>(13, (a,b) -> (int)Math.signum(a.timeEvent - b.timeEvent));
	TramStop[] tramstops = new TramStop[12];
	

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
	// ArrayList<Double> uh = new ArrayList<Double>(128);
	// ArrayList<Double> cs = new ArrayList<Double>(128);

	// ArrayList<Double> heidelTOCS = new ArrayList<Double>(128);
	// ArrayList<Double> paduaTOCS = new ArrayList<Double>(128);
	// ArrayList<Double> kromTOCS = new ArrayList<Double>(128);
	// ArrayList<Double> galgTOCS = new ArrayList<Double>(128);
	// ArrayList<Double> bleekTOCS = new ArrayList<Double>(128);

	// ArrayList<Double> heidelTOUH = new ArrayList<Double>(128);
	// ArrayList<Double> paduaTOUH = new ArrayList<Double>(128);
	// ArrayList<Double> kromTOUH = new ArrayList<Double>(128);
	// ArrayList<Double> galgTOUH = new ArrayList<Double>(128);
	// ArrayList<Double> bleekTOUH = new ArrayList<Double>(128);


	// boolean orderReversed = false;
	TramStop[] tramstops = new TramStop[13];

	public DistributionVariables(String fileName) {

        String csvFile = fileName;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(csvFile));

  			// skip first two lines and check for reversed
            String firstLine = br.readLine();
            //firstLine = br.readLine();
            // if (!firstLine.split(csvSplitBy)[1].equals("AZU")) {
            // 	orderReversed = true;
            // }

            String[] timeslotN;
            int n = 0;

            while ((line = br.readLine()) != null) {

                timeslotN = line.split(csvSplitBy);

                // do something with reversed order
                double[] lambdaArr =new double[64];
                double[] probDep =new double[64];

                for (int i=0;i<64;i++){lambdaArr[i] = Double.parseDouble(timeslotN[i+1]);}
                for (int i=0;i<64;i++){probDep[i] = Double.parseDouble(timeslotN[i+65]);}
                tramstops[n] = new TramStop(n,lambdaArr,probDep);
              	n++;

                // uh.add(n, Double.parseDouble(timeslotN[1]));
                // uh.add(n+64, Double.parseDouble(timeslotN[13]));
                // heidelTOCS.add(n, Double.parseDouble(timeslotN[2]));
                // heidelTOCS.add(n+64, Double.parseDouble(timeslotN[14]));
                // paduaTOCS.add(n, Double.parseDouble(timeslotN[3]));
                // kromTOCS.add(n, Double.parseDouble(timeslotN[4]));
                // galgTOCS.add(n, Double.parseDouble(timeslotN[5]));
                // bleekTOCS.add(n, Double.parseDouble(timeslotN[6]));
                // cs.add(n, Double.parseDouble(timeslotN[7]));
                // bleekTOUH.add(n, Double.parseDouble(timeslotN[8]));
                // galgTOUH.add(n, Double.parseDouble(timeslotN[9]));
                // kromTOUH.add(n, Double.parseDouble(timeslotN[10]));
                // paduaTOUH.add(n, Double.parseDouble(timeslotN[11]));
                // heidelTOUH..add(n, Double.parseDouble(timeslotN[12]));


               //  double hi = umcLambas.get(0);
               //  double no = umcLambas.get(1);

              	// System.out.println(hi);
              	// System.out.println(no);

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



        System.out.println("tramid: "+tramstops[2].id);
        for (int i = 0; i<64; i++){
        	System.out.println("lambdaIn: "+ tramstops[2].lambdaArr[i]+" , lambdaDep: "+tramstops[2].lambdaDep[i]);
        }




    }


}
