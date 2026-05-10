package gui;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

public class  GameVisualizerTest {
    Class<?> gvClass;

    @BeforeEach
    public void init() throws ClassNotFoundException {
        gvClass = Class.forName("gui.GameVisualizer");
    }

    @Test
    public void applyLimitTest() throws Exception {
        Method applyLimits = gvClass.getDeclaredMethod
                ("applyLimits", double.class, double.class, double.class);
        applyLimits.setAccessible(true);
        double firstResult = (double) applyLimits.invoke(null,50.0, 0.0, 100.0);
        Assertions.assertEquals(50.0, firstResult, 1e-9);

        double secondResult = (double) applyLimits.invoke(null,-50.0, 0.0, 100.0);
        Assertions.assertEquals(0.0, secondResult, 1e-9);

        double thirdResult = (double) applyLimits.invoke(null,150.0, 0.0, 100.0);
        Assertions.assertEquals(100.0, thirdResult, 1e-9);
    }

    @Test
    public void lineMoveRobotTest() throws Exception{
        Object gameVisualizer = gvClass.getDeclaredConstructor().newInstance();

        Field posX = gvClass.getDeclaredField("m_robotPositionX");
        Field posY = gvClass.getDeclaredField("m_robotPositionY");
        Field direction = gvClass.getDeclaredField("m_robotDirection");

        posX.setAccessible(true); posX.setDouble(gameVisualizer,0.0);
        posY.setAccessible(true); posY.setDouble(gameVisualizer, 0.0);
        direction.setAccessible(true); direction.setDouble(gameVisualizer, 0.0);

        Method moveRobot = gvClass.getDeclaredMethod
                ("moveRobot", double.class, double.class, double.class);
        moveRobot.setAccessible(true);

        moveRobot.invoke(gameVisualizer, 0.05, 0.0, 10.0);

        double resultX = posX.getDouble(gameVisualizer);
        double resultY = posY.getDouble(gameVisualizer);
        double resultDirection = direction.getDouble(gameVisualizer);

        Assertions.assertEquals(0.5, resultX, 1e-6);
        Assertions.assertEquals(0.0, resultY, 1e-6);
        Assertions.assertTrue(resultDirection >= 0.0 && resultDirection < 2*Math.PI);
    }

    @Test
    public void angularMoveRobotTest() throws Exception {
        Object gameVisualizer = gvClass.getDeclaredConstructor().newInstance();

        Field posX = gvClass.getDeclaredField("m_robotPositionX");
        Field posY = gvClass.getDeclaredField("m_robotPositionY");
        Field direction = gvClass.getDeclaredField("m_robotDirection");

        posX.setAccessible(true); posX.setDouble(gameVisualizer,0.0);
        posY.setAccessible(true); posY.setDouble(gameVisualizer, 0.0);
        direction.setAccessible(true); direction.setDouble(gameVisualizer, 0.0);

        Method moveRobot = gvClass.getDeclaredMethod
                ("moveRobot", double.class, double.class, double.class);
        moveRobot.setAccessible(true);

        moveRobot.invoke(gameVisualizer, 0.1, 0.001, 10.0);

        double resultX = posX.getDouble(gameVisualizer);
        double resultY = posY.getDouble(gameVisualizer);
        double resultDirection = direction.getDouble(gameVisualizer);

        Assertions.assertFalse(Double.isNaN(resultX));
        Assertions.assertFalse(Double.isNaN(resultY));
        Assertions.assertTrue(resultDirection >= 0.0 && resultDirection < 2*Math.PI);

    }



}
