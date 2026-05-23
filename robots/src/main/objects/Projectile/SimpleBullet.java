package objects.Projectile;

import api.GameContext;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class SimpleBullet implements Projectile{
    private final double m_speed;
    private final double m_direction;
    private final boolean ally;

    private double entityX;
    private double entityY;

    private boolean end_flag = false;

    private int damage = 1;

    public SimpleBullet(double x, double y, double speed, double direction, boolean ally) {
        entityX = x;
        entityY = y;

        m_speed = speed;
        m_direction = direction;
        this.ally = ally;
    }

    public void update(GameContext context) {
        double newX = entityX + m_speed * context.getDifferenceTime() * Math.cos(m_direction);
        double newY = entityY + m_speed * context.getDifferenceTime() * Math.sin(m_direction);

//        double newY = entityY - m_speed * Math.cos(m_direction);
//        if (!Double.isFinite(newY))
//        {
//            newY = entityY + m_speed * context.getDifferenceTime() * Math.sin(m_direction);
//        }
        if (isColliding(newX, newY, context.getBorders())) {
            end_flag = true;
            return;
        }
        entityX = newX;
        entityY = newY;

    }

    public double getX() {
        return entityX;
    }

    public double getY() {
        return entityY;
    }

    public double getDirection() {
        return m_direction;
    }

    public int getDamageValue() {
        return damage;
    }
    public boolean isEnd() {
        return end_flag;
    }
    public void setEnd() {
        end_flag = true;
    }
    public boolean isAlly() {
        return ally;
    }

    public void draw(Graphics2D g) {
        int bulletCenterX = round(entityX);
        int bulletCenterY = round(entityY);

        if (ally) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.RED);
        }
        g.fillOval(round(entityX) - 5, round(entityY) - 5, 10, 10);
    }

    private boolean isColliding(double x, double y, List<Shape> gameObjects) {
        java.awt.geom.Rectangle2D bulleHitbox = new Rectangle2D.Double(x - 5, y - 5, 10, 10);
        for (Shape object : gameObjects) {
            if (object.intersects(bulleHitbox)) {
                return true;
            }
        }
        return false;
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
}
