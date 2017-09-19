package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private static final int FILTER_OUT = 15;
  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int filterControl;
  private int distance;
  private int delta = 100;
  
  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    this.filterControl = 0;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {  
	if (distance > 10000){
	  // Do nothing, its an error
	} else if (distance >= 200 && filterControl < FILTER_OUT) {
	  // bad value, do not set the distance var, however do increment the
	  // filter value
	  filterControl++;
	} else if (distance >= 200) {
	  // We have repeated large values, so there must actually be nothing
	  // there: leave the distance alone
	  this.distance = distance;
	} else {	// Make sure distance does not vary too much (reduces bad sample)
	   // distance went below 255: reset filter and leave
	   // distance alone.
	   filterControl = 0;
	   this.distance = distance;
	}
	  
	  
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
    int distError = bandCenter - this.distance;
    
    // If Jerry is within limits, move straight
    if(Math.abs(distError) <= bandwidth){
        WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
        WallFollowingLab.rightMotor.setSpeed(motorHigh);
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
    }
    else if(distError > 0){	// Too close
        WallFollowingLab.leftMotor.setSpeed(motorHigh+delta);
        WallFollowingLab.rightMotor.setSpeed(motorHigh-delta);
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
    }
    else if(distError < 0){	// Too far
        WallFollowingLab.leftMotor.setSpeed(motorHigh-delta); // Start robot moving forward
        WallFollowingLab.rightMotor.setSpeed(motorHigh+delta);
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
    }
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
