package objects.ControlledEntity;

import api.GameContext;
import log.Logger;
import objects.Projectile.Projectile;
import objects.Projectile.SimpleBullet;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class RobotVI implements ControlledEntity {

    private double entityX;
    private double entityY;
    private static final double direction = Math.PI / 2;
    private static final double SPEED = 0.2;
    private static int healthPoint = 10;

    public RobotVI(double X, double Y) {
        this.entityX = X;
        this.entityY = Y;
    }

    public void update(GameContext context) {
        Set<Integer> keys = context.getPressedKeys();
        double difTime = context.getDifferenceTime();

        double diffX = 0;
        double diffY = 0;

        if (keys.contains(KeyEvent.VK_A)) {
            diffX -= SPEED * difTime;
        }
        if (keys.contains(KeyEvent.VK_D)) {
            diffX += SPEED * difTime;
        }
        if (keys.contains(KeyEvent.VK_W)) {
            diffY -= SPEED * difTime;
        }
        if (keys.contains(KeyEvent.VK_S)) {
            diffY += SPEED * difTime;
        }

        double newX = entityX + diffX;
        if (!isColliding(newX, entityY, context.getBorders())) {
            entityX = newX;
        }
        double newY = entityY + diffY;
        if (!isColliding(entityX, newY, context.getBorders())) {
            entityY = newY;
        }

        for (Projectile pjt : context.getProjectiles()) {
            if (distance(entityX, entityY, pjt.getX(), pjt.getY()) < 15 && !pjt.isAlly()
                    && healthPoint > 0) {
                healthPoint -= pjt.getDamageValue();
                pjt.setEnd();
                String message = Integer.toString(healthPoint);
                Logger.debug("Health: " + message);
                break;
            }
        }
        if (healthPoint == 0) {
            onDeathEffect();
            healthPoint = -1;
        }
    }

    public List<Projectile> shoot() {
        List<Projectile> projectiles = new ArrayList<>();
        projectiles.add(new SimpleBullet(entityX, entityY, 0.3, -Math.PI / 2, true));
        return projectiles;
    }

    private void onDeathEffect() {
        Logger.debug("You died");
    }
//------------------------------Графика------------------------------
    public void draw(Graphics2D g) {
        int robotCenterX = round(entityX);
        int robotCenterY = round(entityY);

//        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
//        g.setTransform(t);
        g.setColor(Color.CYAN);
        g.fillOval(robotCenterX, robotCenterY, 10, 30);
        g.setColor(Color.BLACK);
        g.drawOval(robotCenterX, robotCenterY, 10, 30);
        g.setColor(Color.WHITE);
        g.fillOval(robotCenterX, robotCenterY + 10, 5, 5);
        g.setColor(Color.BLACK);
        g.drawOval(robotCenterX, robotCenterY + 10, 5, 5);
    }

//------------------------------Вспомогательные_Методы------------------------------
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

    private boolean isColliding(double x, double y, List<Shape> gameObjects) {
        java.awt.geom.Rectangle2D robotHitbox = new Rectangle2D.Double(x - 10, y - 10, 15, 15);
        for (Shape object : gameObjects) {
            if (object.intersects(robotHitbox)) {
                return true;
            }
        }
        return false;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
//------------------------------Get_Методы------------------------------
    public double getX() {
        return entityX;
    }
    public double getY() {
        return entityY;
    }
    public String getName() {
        return "RobotVI";
    }
}
