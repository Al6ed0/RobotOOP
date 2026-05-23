package api;

import objects.Enemies.Enemy;
import objects.Projectile.Projectile;

import java.awt.*;
import java.util.Set;
import java.util.List;


public interface GameContext {
    Point getMouseTarget();
    Set<Integer> getPressedKeys();

    int getFieldWidth();
    int getFieldHeight();

    double getDifferenceTime();

    List<Shape> getBorders();
    List<Projectile> getProjectiles();
    List<Enemy> getEnemies();
}
