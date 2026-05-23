package objects;

import api.GameContext;
import api.GameModel;
import objects.ControlledEntity.ControlledEntity;
import objects.ControlledEntity.RobotVI;
import objects.Enemies.Enemy;
import objects.Projectile.Projectile;
import objects.Projectile.SimpleBullet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RobotVITest {
    @Test
    public void robotMovementTest() throws Exception{
        GameModel gm = new GameModel();
        ControlledEntity gameRobot = gm.getRobot();

        Field keysField = GameModel.class.getDeclaredField("pressedKeys");

        keysField.setAccessible(true);
        Set<?> internalSet = (Set<?>) keysField.get(gm);

        Assertions.assertTrue(internalSet.isEmpty());
        gm.keyPressed(new KeyEvent(new Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        gm.keyPressed(new KeyEvent(new Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D'));

        Assertions.assertFalse(internalSet.isEmpty());

        GameContext context = new GameContext() {
            @Override
            public Point getMouseTarget() { return null; }
            @Override
            public Set<Integer> getPressedKeys() {
                return (Set<Integer>) internalSet;
            }
            @Override
            public int getFieldWidth() { return 0; }
            @Override
            public int getFieldHeight() { return 0; }
            @Override
            public double getDifferenceTime() { return 10; }
            @Override
            public java.util.List<Shape> getBorders() { return java.util.List.of(); }
            @Override
            public java.util.List<Projectile> getProjectiles() { return java.util.List.of(); }
            @Override
            public java.util.List<Enemy> getEnemies() { return java.util.List.of(); }
        };

        Assertions.assertEquals(300, gameRobot.getX());
        Assertions.assertEquals(500, gameRobot.getY());

        gameRobot.update(context);

        Assertions.assertEquals(302, gameRobot.getX());
        Assertions.assertEquals(498, gameRobot.getY());

        gm.keyReleased(new KeyEvent(new Button(), KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        gm.keyReleased(new KeyEvent(new Button(), KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D'));
        gm.keyPressed(new KeyEvent(new Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_S, 'S'));
        gm.keyPressed(new KeyEvent(new Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A'));

        gameRobot.update(context);

        Assertions.assertEquals(300, gameRobot.getX());
        Assertions.assertEquals(500, gameRobot.getY());

        gm.keyPressed(new KeyEvent(new Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        gm.keyPressed(new KeyEvent(new Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D'));

        Assertions.assertEquals(300, gameRobot.getX());
        Assertions.assertEquals(500, gameRobot.getY());

    }


    @Test
    public void robotAndBordersTest() {
        Rectangle2D wall = new Rectangle2D.Double(100, 100, 50, 50);
        Rectangle2D firstRobotHitbox = new Rectangle2D.Double(90, 90, 20, 20);
        Rectangle2D secondRobotHitbox = new Rectangle2D.Double(10, 10, 20, 20);

        Assertions.assertTrue(wall.intersects(firstRobotHitbox));
        Assertions.assertFalse(wall.intersects(secondRobotHitbox));
    }

    @Test
    public void robotDamageTest() throws NoSuchFieldException, IllegalAccessException {
        GameModel gm = new GameModel();
        ControlledEntity robot = gm.getRobot();

        GameContext context = new GameContext() {
            @Override
            public Point getMouseTarget() { return null; }
            @Override
            public Set<Integer> getPressedKeys() { return Set.of(); }
            @Override
            public int getFieldWidth() { return 0; }
            @Override
            public int getFieldHeight() { return 0; }
            @Override
            public double getDifferenceTime() { return 10; }
            @Override
            public java.util.List<Shape> getBorders() { return java.util.List.of(); }
            @Override
            public java.util.List<Projectile> getProjectiles() {
                java.util.List<Projectile> list = new ArrayList<>();
                list.add(new SimpleBullet(300, 500, 0, 0, false));
                return list;
            }
            @Override
            public java.util.List<Enemy> getEnemies() { return List.of(); }
        };

        Field hp = RobotVI.class.getDeclaredField("healthPoint");
        hp.setAccessible(true);
        int internalHp = (int) hp.get(robot);
        Assertions.assertEquals(10, internalHp);

        robot.update(context);

        internalHp = (int) hp.get(robot);
        Assertions.assertEquals(9, internalHp);

        for (int i = 0; i < 9; i++) {
            robot.update(context);
        }
        internalHp = (int) hp.get(robot);
        Assertions.assertEquals(-1, internalHp);
    }

}
