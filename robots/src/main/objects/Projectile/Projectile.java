package objects.Projectile;

import api.GameContext;

import java.awt.*;

public interface Projectile {
    void update(GameContext context);
    double getX();
    double getY();
    double getDirection();
    void draw(Graphics2D g);
    boolean isEnd();
    void setEnd();
    boolean isAlly();

    int getDamageValue();
}