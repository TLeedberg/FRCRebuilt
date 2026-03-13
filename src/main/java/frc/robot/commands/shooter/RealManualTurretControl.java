package frc.robot.commands.shooter;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import frc.robot.OI;
import frc.robot.subsystems.Shooter;
import frc.robot.testingdashboard.Command;

public class RealManualTurretControl extends Command {
    private final Shooter m_Shooter;
    private final Supplier<Double> m_speed;

    public RealManualTurretControl() {
        super(Shooter.getInstance(), "Control", "RealManualTurretControl");
        
        m_Shooter = Shooter.getInstance();
        m_speed = OI.getInstance().getOperatorController()::getLeftX;

        addRequirements(m_Shooter);
    }

    @Override
    public void initialize() {
        m_Shooter.setTurretRobotRelative(true);
        m_Shooter.setTurretControl(false);
    }

    @Override
    public void execute() {
        m_Shooter.setTurretRawSpeed(MathUtil.applyDeadband(-m_speed.get(), 0.05));
    }

    @Override
    public void end(boolean interrupted) {
        m_Shooter.setTurretControl(true);
    }
}
