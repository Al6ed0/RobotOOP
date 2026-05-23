package objects.Enemies;

import api.GameContext;
import objects.Projectile.Projectile;
import objects.Projectile.SimpleBullet;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShooterDummy implements Enemy {
    private boolean end_flag = false;
    private double entityX;
    private double entityY;
    private double m_speed;
    private final Random random = new Random();
    double moveTimer = 500;
    private int healthPoint = 4;
    private int edge = -1;


    public ShooterDummy(double x, double y, double speed) {
        entityX = x;
        entityY = y;
        m_speed = speed;
    }

    public void update(GameContext context) {
        moveTimer -= context.getDifferenceTime();
        if (moveTimer == 0 || edge == -1) {
            edge = random.nextInt(2);
            resetMoveTimer();
        }
        double newX;

        if (edge == 0) {
            newX = entityX + m_speed * context.getDifferenceTime();
            if (isColliding(newX, entityY, context.getBorders())) {
                edge = 1 - edge;
                resetMoveTimer();
            }
        } else {
            newX = entityX - m_speed * context.getDifferenceTime();
            if (isColliding(newX, entityY, context.getBorders())) {
                edge = 1 - edge;
                resetMoveTimer();
            }
        }

        for (Projectile pjt : context.getProjectiles()) {
            if (distance(entityX, entityY, pjt.getX(), pjt.getY()) < 25 && pjt.isAlly()) {
                healthPoint -= pjt.getDamageValue();
                pjt.setEnd();
                break;
            }
        }

        if (healthPoint == 0) {
            end_flag = true;
            return;
        }
        entityX = newX;
    }

    public List<Projectile> attack() {
        List<Projectile> projectiles = new ArrayList<>();
        projectiles.add(new SimpleBullet(entityX, entityY, 0.1, Math.PI/2, false));
        projectiles.add(new SimpleBullet(entityX, entityY, 0.1, Math.PI/3, false));
        projectiles.add(new SimpleBullet(entityX, entityY, 0.1, 2 * Math.PI/3, false));
        return projectiles;
    }
    //----------------------------Утилитарные методы------------------
    private boolean isColliding(double x, double y, List<Shape> gameObjects) {
        java.awt.geom.Rectangle2D enemyHitbox = new Rectangle2D.Double(x - 25, y - 25, 50, 50);
        for (Shape object : gameObjects) {
            if (object.intersects(enemyHitbox)) {
                return true;
            }
        }
        return false;
    }

    private void resetMoveTimer() {
        moveTimer = 500;
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    //------------------------------Графика------------------------------
    public void draw(Graphics2D g) {
        int centerX = round(entityX);
        int centerY = round(entityY);

        g.setColor(Color.RED);
        g.fillRect(centerX - 25, centerY - 25, 50, 50);
    }

    //---------------------------GET_Методы----------------------
    public double getX() {
        return entityX;
    }
    public double getY() {
        return entityY;
    }
    public boolean isEnd() {
        return end_flag;
    }
}
