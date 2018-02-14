/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6763.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	AHRS navx = new AHRS(SerialPort.Port.kUSB);
	DifferentialDrive myRobot = new DifferentialDrive(new Spark(0), new Spark(2));
	Joystick stick = new Joystick(0);
	Spark climber = new Spark(7);
	
	Encoder leftEncoder = new Encoder(0, 1);
	Encoder rightEncoder = new Encoder(2, 3);
	
	float ticksPerInch = 106;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		rightEncoder.setReverseDirection(true);
		
		CameraServer.getInstance().startAutomaticCapture();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		
		leftEncoder.reset();
		rightEncoder.reset();
		
		navx.reset();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				if(leftEncoder.get() < 32651) {
					SmartDashboard.putString("Mode", "Drive straight");
					accurateDrive(navx, 0.6, -90, 2);
				}
				else if(navx.getYaw() < 90) {
					SmartDashboard.putString("Mode", "Turning right");
					myRobot.tankDrive(0.5, -0.5);
				}
				else {
					SmartDashboard.putString("Mode", "Stopped");
					myRobot.stopMotor();
				}
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		myRobot.arcadeDrive(-stick.getY(), stick.getX());
		
		climber.set(-stick.getRawAxis(3));
	}
	
	public void testInit() {
		leftEncoder.reset();
	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		System.out.println(leftEncoder.get());
	}
	
	public void accurateDrive(AHRS ahrs, double speed, double targetAngle, int tolerence) {
		if(ahrs.getYaw() < targetAngle - tolerence) {
			System.out.println("Too far left");
			myRobot.tankDrive(speed, speed / 2);
		}
		else if(ahrs.getYaw() > targetAngle + tolerence) {
			System.out.println("Too far right");
			myRobot.tankDrive(speed / 2, speed);
		}
		else {
			System.out.println("Good");
			myRobot.tankDrive(speed, speed);
		}
	}
}
