package objects;

import log.Logger;

import java.awt.*;


public class SimpleRobot {
    private volatile double m_robotPositionX;
    private volatile double m_robotPositionY;
    private volatile double m_robotDirection;

    private volatile double m_targetPositionX;
    private volatile double m_targetPositionY;

    private final double m_maxVelocity;
    private final double m_maxAngularVelocity;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGUlAR_VELOCITY = 0.002;
    private static final double AVG_ANGUlAR_VELOCITY = 0.001;

    private final double minimalDistance;

    private boolean distanceFlag = false;

    public SimpleRobot(double positionX, double positionY, double direction,
                       double maxVelocity, double maxAngularVelocity) {
        this.m_robotPositionX = positionX;
        this.m_robotPositionY = positionY;
        this.m_robotDirection = direction;

        this.m_targetPositionX = positionX;
        this.m_targetPositionY = positionY;

        this.m_maxVelocity = maxVelocity;
        this.m_maxAngularVelocity = maxAngularVelocity;

        this.minimalDistance = getMinimalDistance();
    }

    public SimpleRobot(double positionX, double positionY, double direction) {
        this.m_robotPositionX = positionX;
        this.m_robotPositionY = positionY;
        this.m_robotDirection = direction;

        this.m_targetPositionX = positionX;
        this.m_targetPositionY = positionY;

        this.m_maxVelocity = MAX_VELOCITY;
        this.m_maxAngularVelocity = MAX_ANGUlAR_VELOCITY;

        this.minimalDistance = getMinimalDistance();
    }



    public void setTargetPosition(Point p)
    {
        distanceFlag = false;
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    public void onUpdateEvent()
    {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5)
        {
            distanceFlag = false;
            return;
        }
        if (distance > minimalDistance && !distanceFlag) {
            distanceFlag = true;
        }
        double velocity = m_maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;

        double tolerance = 0.05;
        double angleDifference = asNormalizedRadians(angleToTarget - m_robotDirection);

        if (angleDifference < tolerance || angleDifference > 2 * Math.PI - tolerance) {
            angularVelocity = 0;
        } else if (angleDifference < Math.PI) {
            if (distanceFlag) {
                angularVelocity = m_maxAngularVelocity;
            } else {
                angularVelocity = -m_maxAngularVelocity;
            }
        } else {
            if (distanceFlag) {
                angularVelocity = -m_maxAngularVelocity;
            } else {
                angularVelocity = m_maxAngularVelocity;
            }
        }

        moveRobot(velocity, angularVelocity, 10, 500, 500);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration,
                           double fieldWidth, double fieldHeight)
    {
        velocity = applyLimits(velocity, 0, m_maxVelocity);

        angularVelocity = applyLimits(angularVelocity, -m_maxAngularVelocity, m_maxAngularVelocity);

        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));

        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }

        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }

        m_robotPositionX = applyLimits(newX, 0, fieldWidth);
        m_robotPositionY = applyLimits(newY, 0, fieldHeight);
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

//-----------------------Вспомогательные_Методы-------------------------

    private double getMinimalDistance() {
        return (200 / this.m_maxAngularVelocity) * AVG_ANGUlAR_VELOCITY;
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }
//-----------------------Get_Методы-------------------------
    public double getRobotX() {
        return this.m_robotPositionX;
    }
    public double getRobotY() {
        return this.m_robotPositionY;
    }
    public double getRobotDirection() {
        return this.m_robotDirection;
    }
    public int getTargetPositionX() {
        return round(this.m_targetPositionX);
    }
    public int getTargetPositionY() {
        return round(this.m_targetPositionY);
    }
}
