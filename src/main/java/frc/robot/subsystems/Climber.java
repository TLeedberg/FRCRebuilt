// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.SparkFlexConfig;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.robot.testingdashboard.TDNumber;

public class Climber extends SubsystemBase {
  /** Creates a new Climber. */
  private static Climber m_Climber;

  double m_climberP = Constants.ClimberConstants.kClimberP;
  double m_climberI = Constants.ClimberConstants.kClimberI;
  double m_climberD = Constants.ClimberConstants.kClimberD;
  double m_climberkS = Constants.ClimberConstants.kClimberkS;
  double m_climberkG = Constants.ClimberConstants.kClimberkG;
  double m_climberkV = Constants.ClimberConstants.kClimberkV;
  double m_climberkA = Constants.ClimberConstants.kClimberkA;

  SparkFlex m_climberLeftSparkFlex;
  SparkFlex m_climberRightSparkFlex;

  SparkClosedLoopController m_climberClosedLoopController;
  RelativeEncoder m_climberMotorEncoder;

  // Making Trapezoid Profile & FeedForoward
  ElevatorFeedforward m_climberFeedForwardController;
  TrapezoidProfile m_climberProfile;
  TrapezoidProfile.State m_climberState;
  TrapezoidProfile.State m_climberSetpoint;

  SparkFlexConfig m_leftSparkFlexConfig;

  private Climber() {
    super("Climber");

    if (RobotMap.C_ENABLED) {
      // Setup Climber (Yes this is just Ctrl C & V)
      m_leftSparkFlexConfig = new SparkFlexConfig();
      SparkFlexConfig rightElevatorSparkFlexConfig = new SparkFlexConfig();

      m_leftSparkFlexConfig.closedLoop.pid(Constants.ClimberConstants.kClimberP, Constants.ClimberConstants.kClimberI,
          Constants.ClimberConstants.kClimberD);
      m_leftSparkFlexConfig.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
      m_leftSparkFlexConfig.closedLoop.positionWrappingEnabled(false);

      m_climberClosedLoopController = m_climberLeftSparkFlex.getClosedLoopController();
      m_climberMotorEncoder = m_climberLeftSparkFlex.getEncoder();
      m_climberMotorEncoder.setPosition(0);

      m_climberFeedForwardController = new ElevatorFeedforward(Constants.ClimberConstants.kClimberkS, Constants.ClimberConstants.kClimberkG, Constants.ClimberConstants.kClimberkV, Constants.ClimberConstants.kClimberkA);
      m_climberProfile = new TrapezoidProfile(new TrapezoidProfile.Constraints(
        Constants.ClimberConstants.kClimberMaxVelocity,
        Constants.ClimberConstants.kClimberMaxAcceleration
      ));
      m_climberSetpoint = new TrapezoidProfile.State(m_climberMotorEncoder.getPosition(), 0.0);
      m_climberState = new TrapezoidProfile.State(m_climberMotorEncoder.getPosition(), 0.0);
    }
  }

  public static Climber getInstance() {
    if (m_Climber == null) {
      m_Climber = new Climber();
    }
    return m_Climber;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
