package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import frc.robot.RobotMap;
import frc.robot.testingdashboard.TDNumber;
import frc.robot.testingdashboard.SubsystemBase;



public class Intake extends SubsystemBase{

    SparkMax m_ISparkMax1;
    SparkMax m_ISparkMax2;
    SparkMaxConfig m_SparkMaxConfig;

    TDNumber td_currentOutput;

    private static Intake m_Intake = null;

    private Intake(){
        super("Intake");
        if (RobotMap.I_ENABLED == true){
            m_ISparkMax1 = new SparkMax(RobotMap.I_MOTOR_ONE, MotorType.kBrushless); //unsure if we are using brushed or brushless but we used brushless on leaflet
            m_ISparkMax2 = new SparkMax(RobotMap.I_MOTOR_TWO, MotorType.kBrushless);
            m_SparkMaxConfig = new SparkMaxConfig();
            m_SparkMaxConfig
                .idleMode(IdleMode.kCoast)
                .smartCurrentLimit(25, 60); //settings from offseason robot, unsure what they mean

            m_ISparkMax1.configure(m_SparkMaxConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

            td_currentOutput = new TDNumber(this, "Intake", "Ground Intake");
                
        }
    }

    public static Intake getInstance() {
        if (m_Intake == null) {
            m_Intake = new Intake();
        }
        return m_Intake;
    }
    public void spinIn(double speed) {
        if (m_ISparkMax1 != null) {
            m_ISparkMax1.set(speed);
        }
        if (m_ISparkMax2 != null) {

            if (RobotMap.I_OPPOSITE == true){
                m_ISparkMax2.set(-1 * speed);
            } else {
                m_ISparkMax2.set(speed);
            }  
        } 
    }
    public void spinOut(double speed) {
        if (m_ISparkMax1 != null) {
            m_ISparkMax1.set(-1 * speed);
        }
        if (m_ISparkMax2 != null) {

            if (RobotMap.I_OPPOSITE == true){
                m_ISparkMax2.set(speed);
            } else {
               m_ISparkMax2.set(-1 * speed); 
            }  
        } 
    }
    public void stop() {
        if (m_ISparkMax1 != null) {
            m_ISparkMax1.set(-0);
        }
        if (m_ISparkMax2 != null) {
            m_ISparkMax2.set(-0);
        }
    }

    @Override
    public void periodic() {
        if (RobotMap.I_ENABLED) {
            td_currentOutput.set(m_ISparkMax1.getOutputCurrent());
            td_currentOutput.set(m_ISparkMax2.getOutputCurrent());
        }
        super.periodic();
    }
    
}
