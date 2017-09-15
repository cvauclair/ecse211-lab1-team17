package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 20;
  private static final int MAX_CORRECTION = 100;
  private static final int PROPORTIONAL_CONSTANT = 2;

  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int filterControl;
  private int distError;
  
  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;
    
    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {

    // rudimentary filter - toss out invalid samples corresponding to null
    // signal.
    // (n.b. this was not included in the Bang-bang controller, but easily
    // could have).
    //
//    if (distance >= 255 && filterControl < FILTER_OUT) {
//      // bad value, do not set the distance var, however do increment the
//      // filter value
//      filterControl++;
//    } else if (distance >= 255) {
//      // We have repeated large values, so there must actually be nothing
//      // there: leave the distance alone
//      this.distance = distance;
//    } else {
//      // distance went below 255: reset filter and leave
//      // distance alone.
//      filterControl = 0;
//      this.distance = distance;
//    }

    // TODO: process a movement based on the us distance passed in (P style)
    
	this.distance = distance;
    distError = this.distance - bandCenter;
	  
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
    else if(distError < 0){	// Too far
        WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED-correction(distError)); // Start robot moving forward
        WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED+correction(distError));
        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
    }
  }

  private static int correction(int distError){
	  return (int) (25 + 125/(1 + Math.exp(-1.0 * (Math.abs(distError)-20)/10)));
//	  int correction = PROPORTIONAL_CONSTANT * distError;
//	  
//	  // Prevent correction from being greater than the base speed of the motors
//	  return correction > MAX_CORRECTION ? MAX_CORRECTION : correction;
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
