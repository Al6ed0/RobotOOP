package objects.Enemies;

import api.GameContext;
import objects.Projectile.Projectile;
import java.util.List;
import java.awt.*;

public interface Enemy {
    void update(GameContext context);
    double getX();
    double getY();
    boolean isEnd();

    List<Projectile> attack();

    void draw(Graphics2D g);
}
