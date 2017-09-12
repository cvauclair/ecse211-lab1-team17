package ca.mcgill.ecse211.wallfollowing;

import lejos.robotics.SampleProvider;

/**
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20mS, and that the thread sleeps for 50 mS at the end of each loop, then
 * one cycle through the loop is approximately 70 mS. This corresponds to a sampling rate of 1/70mS
 * or about 14 Hz.
 */
public class UltrasonicPoller extends Thread {
  private SampleProvider us;
  private UltrasonicController cont;
  private float[] usData;

  public UltrasonicPoller(SampleProvider us, float[] usData, UltrasonicController cont) {
    this.us = us;
    this.cont = cont;
    this.usData = usData;
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
   * [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    int distance;
    float sum;
    int totalCounted;
    while (true) {
       sum = 0;
       totalCounted = 0;
       // Get 10 data points
       for(int i = 0; i < 10; i++){
    	   us.fetchSample(usData, 0); // acquire data	
    	   if(usData[0] < 1.0){
    		   sum += usData[0];
    		   totalCounted++;
    	   }
       }
    	
      // Average out the results
//      sum = 0;
//      totalCounted = 0;
//      for(float dist : usData){
//    	  // Only get data if distance is not too large
//    	  if(dist < 1.0){
//        	  sum += dist;
//        	  totalCounted++;
//    	  }
//      }
//      distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
      distance = (int) (100.0 * sum/totalCounted);
      
      cont.processUSData(distance); // now take action depending on value
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }

}
