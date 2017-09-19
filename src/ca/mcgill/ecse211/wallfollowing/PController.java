package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 15;
//  private static final int VARIATION_FILTER = 10;
  private static final int MAX_CORRECTION = 200;
  private static final int CORRECTION_FACTOR = 6;

  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int oldDistance;
  private int filterControl;
  private int distError;
  private int errorCMA;	// Error cumulative moving average
  private int CMACounter;
  
  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;
    this.oldDistance = 0;
    
    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
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

//	this.distance = distance;
    // TODO: process a movement based on the us distance passed in (P style)
    
//    if(CMACounter > )
    
//    errorCMA = (int)((bandCenter - this.distance) + errorCMA * CMACounter)/(CMACounter+1);
//    CMACounter++;
    
    distError = bandCenter - this.distance;
    // If Jerry is within limits, move straight
    if(Math.abs(distError) <= bandWidth){
        WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Start robot moving forward
        WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
    }
    else if(distError > 0){	// Too close
        WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED+correction(distError));
        WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED-correction(distError));
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
    }
    else if(distError< 0){	// Too far
        WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED-correction(distError)); // Start robot moving forward
        WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED+correction(distError));
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
    }
    
    oldDistance = this.distance;
  }

  private static int correction(int distError){
//	  return (int) (150/(1 + Math.exp(-1.0 * (Math.abs(distError)-10)/4)));
	  int correction = 0;
	  if(distError > 16) {
		  // If too close to wall -> big correction
//		  correction = 12 * Math.abs(distError);
		  return 300;
	  } else if(distError < 100){
		  return 300;
	  }
	  else {
		  correction = 6 * Math.abs(distError);
	  }
	  // Prevent correction from being greater than the base speed of the motors
	  return correction > MAX_CORRECTION ? MAX_CORRECTION : correction;
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
