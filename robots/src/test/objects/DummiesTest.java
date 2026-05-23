package objects;

import api.GameContext;
import objects.Enemies.Enemy;
import objects.Enemies.ShooterDummy;
import objects.Enemies.SimpleDummy;
import objects.Projectile.Projectile;
import objects.Projectile.SimpleBullet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DummiesTest {
    @Test
    public void enemyMoveTest() throws NoSuchFieldException, IllegalAccessException {

        ShooterDummy enemy = new ShooterDummy(200, 200, 5);
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
                return java.util.List.of();
            }
            @Override
            public java.util.List<Enemy> getEnemies() { return java.util.List.of(); }
        };

        Assertions.assertEquals(200, enemy.getX());
        Assertions.assertEquals(200, enemy.getY());

        enemy.update(context);

        Assertions.assertNotEquals(200, enemy.getX());
        Assertions.assertEquals(200, enemy.getY());
    }

    @Test
    public void enemyAttackTest() {
        SimpleDummy dummy = new SimpleDummy(0, 0, 0);
        ShooterDummy sDummy = new ShooterDummy(0, 0, 0);

        Assertions.assertTrue(dummy.attack().isEmpty());
        Assertions.assertFalse(sDummy.attack().isEmpty());
    }

    @Test
    public void enemyDamageTest() throws NoSuchFieldException, IllegalAccessException {
        SimpleDummy dummy = new SimpleDummy(0, 0, 0);

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
                list.add(new SimpleBullet(0, 0, 0, 0, true));
                return list;
            }
            @Override
            public java.util.List<Enemy> getEnemies() { return List.of(); }
        };
        Field hp = SimpleDummy.class.getDeclaredField("healthPoint");
        hp.setAccessible(true);
        int internalHp = (int) hp.get(dummy);
        Assertions.assertEquals(4, internalHp);

        dummy.update(context);

        internalHp = (int) hp.get(dummy);
        Assertions.assertEquals(3, internalHp);

        for (int i = 0; i < 3; i++) {dummy.update(context);}

        Assertions.assertTrue(dummy.isEnd());

    }
}
