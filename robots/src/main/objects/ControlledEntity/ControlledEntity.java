package objects.ControlledEntity;

import api.GameContext;
import objects.Projectile.Projectile;
import java.util.List;
import java.awt.*;

public interface ControlledEntity {
    String getName();
    void draw(Graphics2D g);
    void update(GameContext context);

    double getX();
    double getY();

    List<Projectile> shoot();
}
