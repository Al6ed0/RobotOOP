package gui;


import objects.SimpleRobot;

import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{
    private final Timer m_timer = initTimer();
    final private static double PI = Math.PI;

    SimpleRobot robot = new SimpleRobot(100, 100, 0, 0.1, 0.002);

    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    
    public GameVisualizer() 
    {

        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                robot.onUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                robot.setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

//    private void onMouseClick(Point screenPoint) {
//        double scale = Math.min((double) getWidth() / VIRTUAL_WIDTH,
//                (double) getHeight() / VIRTUAL_HEIGHT);
//        double offsetX = (getWidth() - VIRTUAL_WIDTH * scale) / 2;
//        double offsetY = (getHeight() - VIRTUAL_HEIGHT * scale) / 2;
//
//        int virtualX = (int) ((screenPoint.x - offsetX) / scale);
//        int virtualY = (int) ((screenPoint.y - offsetY) / scale);
//
//        this.m_targetPositionX = virtualX;
//        this.m_targetPositionY = virtualY;
//        distanceFlag = false;
//    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        drawRobot(g2d, round(robot.getRobotX()), round(robot.getRobotY()),
                robot.getRobotDirection());
        drawTarget(g2d, robot.getTargetPositionX(), robot.getTargetPositionY());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = round(robot.getRobotX());
        int robotCenterY = round(robot.getRobotY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.CYAN);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
