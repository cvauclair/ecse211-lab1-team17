package ca.mcgill.ecse211.wallfollowing;

import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;

/**
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20mS, and that the thread sleeps for 50 mS at the end of each loop, then
 * one cycle through the loop is approximately 70 mS. This corresponds to a sampling rate of 1/70mS
 * or about 14 Hz.
 */
public class UltrasonicPoller extends Thread implements TimerListener {
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
    int realDistance;
    float sum = 0, distance;
    

    
    while (true) {
      sum = 0;
      distance = 0;	
    	
      for(int i = 0; i < 8; i++){
    	us.fetchSample(usData, 0);
      	sum += usData[0] * 100.0;
      }
        
      distance = sum/8;
      realDistance = (int)(Math.cos(45) * distance);
        
      cont.processUSData(realDistance); // now take action depending on value
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }
  
  public void timedOut(){
	  int distance;
	  float sum = 0;
	  
	  // Take 8 samples and average them out
	  for(int i = 0; i < 8; i++){
		  us.fetchSample(usData, 0);
		  sum += usData[0];
	  }
	  
	  distance = (int)(Math.cos(45) * 100.0 * sum/8);
      
      cont.processUSData(distance); // now take action depending on value
  }

}
