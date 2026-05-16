package objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class SimpleRobotTest {


    Class<?> robotClass;


    @BeforeEach
    public void init() throws ClassNotFoundException {
        robotClass = Class.forName("objects.SimpleRobot");
    }

    @Test
    public void applyLimitTest() throws Exception {
        Method applyLimits = robotClass.getDeclaredMethod
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
        SimpleRobot robot = new SimpleRobot(0.0, 0.0, 0.0);

        Method moveRobot = robot.getClass().getDeclaredMethod
                ("moveRobot", double.class, double.class, double.class,
                        double.class, double.class);
        moveRobot.setAccessible(true);

        moveRobot.invoke(robot, 0.05, 0.0, 10.0, 400, 400);

        double resultX = robot.getRobotX();
        double resultY = robot.getRobotY();
        double resultDirection = robot.getRobotDirection();

        Assertions.assertEquals(0.5, resultX, 1e-6);
        Assertions.assertEquals(0.0, resultY, 1e-6);
        Assertions.assertTrue(resultDirection >= 0.0 && resultDirection < 2*Math.PI);
    }

    @Test
    public void angularMoveRobotTest() throws Exception {
        SimpleRobot robot = new SimpleRobot(0.0, 0.0, 0.0);

        Method moveRobot = robotClass.getDeclaredMethod
                ("moveRobot", double.class, double.class, double.class,
                        double.class, double.class);
        moveRobot.setAccessible(true);

        moveRobot.invoke(robot, 0.1, 0.001, 10.0, 400, 400);

        double resultX = robot.getRobotX();
        double resultY = robot.getRobotY();
        double resultDirection = robot.getRobotDirection();

        Assertions.assertFalse(Double.isNaN(resultX));
        Assertions.assertFalse(Double.isNaN(resultY));
        Assertions.assertTrue(resultDirection >= 0.0 && resultDirection < 2*Math.PI);
    }

    @Test
    public void moveToTargetTest() throws NoSuchMethodException {
        SimpleRobot robot = new SimpleRobot(100.0, 100.0, 0.0, 0.1, 0.001);

        robot.setTargetPosition(new Point(300, 100));
        for (int i = 0; i < 200; i++) {
            robot.onUpdateEvent();
        }
        Assertions.assertEquals(300, robot.getRobotX(), 1e-9);
        Assertions.assertEquals(100, robot.getRobotY(), 1e-9);
        Assertions.assertEquals(0.0, robot.getRobotDirection());

        robot.setTargetPosition(new Point(300, 300));
        for (int i = 0; i < 500; i++) {
            robot.onUpdateEvent();
        }

        Assertions.assertEquals(300, robot.getRobotX(), 0.5);
        Assertions.assertEquals(300, robot.getRobotY(), 0.5);
        Assertions.assertEquals(Math.PI, robot.getRobotDirection(), 0.05);

        robot.setTargetPosition(new Point(300, 200));
        for (int i = 0; i < 1000; i++) {
            robot.onUpdateEvent();
        }

        Assertions.assertEquals(300, robot.getRobotX(), 0.5);
        Assertions.assertEquals(200, robot.getRobotY(), 0.5);
        Assertions.assertTrue(robot.getRobotDirection() >= 0.0
                && robot.getRobotDirection() < 2*Math.PI);
    }

}
