package frc.robot.commands.shooter;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Shooter;
import frc.robot.testingdashboard.Command;

public class ShootMap extends Command {
    private final Shooter m_Shooter;
    private final Drive m_Drive;

    public final ShootMapSetpoint m_pointMin;
    public final ShootMapSetpoint m_pointMax;

    public static class ShootMapSetpoint {
        public final Pose2d pose;
        public final double flywheel;
        public final double hood;
        public final double turret;

        public ShootMapSetpoint(Pose2d robotPose, double flywheel, double hood, double turret) {
            this.pose = robotPose;
            this.flywheel = flywheel;
            this.hood = hood;
            this.turret = turret;
        }
    }

    public ShootMap(ShootMapSetpoint a, ShootMapSetpoint b) {
        super(Shooter.getInstance(), "Shooting", "ShootMap", false);

        m_Shooter = Shooter.getInstance();
        m_Drive = Drive.getInstance();

        assert  (a.pose.getX() <= b.pose.getX() &&
                 a.pose.getY() <= b.pose.getY()) ||
                (a.pose.getX() >= b.pose.getX() &&
                 a.pose.getY() >= b.pose.getY());

        if (a.pose.getX() < b.pose.getX()) {
            m_pointMin = a;
            m_pointMax = b;
        } else {
            m_pointMin = b;
            m_pointMax = a;
        }

        addRequirements(m_Shooter);
    }

    private Pose2d clampToPoints(Pose2d in) {
        if (in.getX() < m_pointMin.pose.getX())
            in = new Pose2d(new Translation2d(m_pointMin.pose.getX(), in.getY()), in.getRotation());
        else if (in.getX() > m_pointMax.pose.getX())
            in = new Pose2d(new Translation2d(m_pointMax.pose.getX(), in.getY()), in.getRotation());

        if (in.getY() < m_pointMin.pose.getY())
            in = new Pose2d(new Translation2d(in.getX(), m_pointMin.pose.getY()), in.getRotation());
        else if (in.getY() > m_pointMax.pose.getY())
            in = new Pose2d(new Translation2d(in.getX(), m_pointMax.pose.getY()), in.getRotation());

        return in;
    }

    private Pose2d lerpPose2d(Pose2d a, Pose2d b, double t) {
        Translation2d ta = a.getTranslation();
        Translation2d tb = b.getTranslation();
        Translation2d ti = ta.plus(tb.minus(ta).times(t));
        
        Rotation2d ra = a.getRotation();
        Rotation2d rb = b.getRotation();
        Rotation2d ri = ra.plus(rb.minus(ra).times(t));

        return new Pose2d(ti, ri);
    }

    private ShootMapSetpoint interpolate(double weight) {
        return new ShootMapSetpoint(
            lerpPose2d(m_pointMin.pose, m_pointMax.pose, weight),
            MathUtil.interpolate(m_pointMin.flywheel, m_pointMax.flywheel, weight),
            MathUtil.interpolate(m_pointMin.hood, m_pointMax.hood, weight),
            MathUtil.interpolate(m_pointMin.turret, m_pointMax.turret, weight));
    }

    @Override
    public void initialize() {}
    
    @Override
    public void execute() {
        m_Shooter.setTurretRobotRelative(true);

        Pose2d pose = m_Drive.getPose();
        pose = clampToPoints(pose);

        double weightMin = pose.getTranslation().getDistance(m_pointMin.pose.getTranslation());
        double weightMax = pose.getTranslation().getDistance(m_pointMax.pose.getTranslation());
        double weight = weightMin/(weightMin+weightMax);

        ShootMapSetpoint setpoint = interpolate(weight);

        double intended = setpoint.pose.getRotation().getRadians();
        double actual = pose.getRotation().getRadians();
        double error = intended - actual;
        error = MathUtil.clamp(error, Math.toRadians(-30), Math.toRadians(30));
        m_Shooter.setTurretTarget(setpoint.turret - error, 0);

        m_Shooter.setFlywheelTarget(setpoint.flywheel);
        m_Shooter.setHoodTarget(setpoint.hood);
    }

    @Override
    public void end(boolean interrupted) {
        m_Shooter.setHoodTarget(0);
        m_Shooter.setFlywheelTarget(0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
