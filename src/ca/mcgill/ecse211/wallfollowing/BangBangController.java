package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int distance;
  private int delta = 50;

  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {
    this.distance = distance;
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
    int distError = bandCenter - distance;
    
    // If bot is within limits, move straight
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
