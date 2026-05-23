package api;
import java.awt.*;

import objects.Enemies.SimpleDummy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;


public class GameModelTest {


    @Test
    public void newProjectilesTest() throws Exception {
        GameModel gm = new GameModel();
        Field projectilesField = GameModel.class.getDeclaredField("projectiles");

        projectilesField.setAccessible(true);
        List<?> internalList = (List<?>) projectilesField.get(gm);

        Assertions.assertTrue(internalList.isEmpty());

        gm.bulletHell();

        Assertions.assertFalse(internalList.isEmpty());
    }

    @Test
    public void summonMobTest() throws Exception {
        GameModel gm = new GameModel();
        Field enemiesField = GameModel.class.getDeclaredField("enemies");

        enemiesField.setAccessible(true);
        List<?> internalList = (List<?>) enemiesField.get(gm);

        Assertions.assertTrue(internalList.isEmpty());

        gm.spawnMob(new SimpleDummy(0, 0, 0));

        Assertions.assertFalse(internalList.isEmpty());
    }
}
