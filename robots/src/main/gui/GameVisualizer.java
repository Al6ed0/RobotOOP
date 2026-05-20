package gui;


import log.Logger;
import objects.ControlledEntity.ControlledEntity;
import objects.ControlledEntity.RobotVI;
import objects.Enemies.Enemy;
import objects.Enemies.SimpleDummy;
import objects.Projectile.Projectile;
import objects.SimpleRobot;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;
import java.util.Timer;

import javax.swing.*;

public class GameVisualizer extends JPanel
{
    private final Timer m_timer = initTimer();
    private final static int VIRTUAL_WIDTH = 600;
    private final static int VIRTUAL_HEIGHT = 600;
    private Point mouseTarget = null;

    private List<Shape> borders;
    private List<Projectile> projectiles = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();

    ControlledEntity robot = new RobotVI(300, 500);

    private static Timer initTimer()
    {
        return new Timer("events generator", true);
    }

    private final Set<Integer> pressedKeys = new HashSet<>();

    public GameVisualizer()
    {
        setupBorders();
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run() {
                EventQueue.invokeLater(GameVisualizer.this::repaint);
            }
        }, 0, 16);

        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run() { onUpdateEvent(); }
        }, 0, 10);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (projectiles){
                    projectiles.addAll(robot.shoot());
                    for (Enemy enemy : enemies) {
                        projectiles.addAll(enemy.attack());
                    }
                }
            }
        }, 0, 1000);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                mouseClick(e.getPoint());
                repaint();
            }
        });

        setDoubleBuffered(true);
        setupKeyBinds();

    }

    private void setupBorders() {
        this.borders = new ArrayList<>();
        borders.add(new Rectangle(0,0,600, 10));
        borders.add(new Rectangle(0,590,600, 10));
        borders.add(new Rectangle(590,0,10, 600));
        borders.add(new Rectangle(0,0,10, 600));
    }

    private void setupKeyBinds() {
        int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D};

        for (int keyCode : keys) {
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(keyCode, 0, false),
                    "press" + keyCode);
            getActionMap().put("press" + keyCode, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pressedKeys.add(keyCode);
                }
            });

            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(keyCode, 0 , true),
                    "release" + keyCode
            );
            getActionMap().put("release" + keyCode, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pressedKeys.remove(keyCode);
                }
            });
        }
    }

    protected void onUpdateEvent() {
        if (robot == null) { return; }
        GameContext context = new GameContext() {
            @Override
            public Point getMouseTarget() {
                return mouseTarget;
            }

            @Override
            public Set<Integer> getPressedKeys() {
                return new HashSet<>(pressedKeys);
            }

            @Override
            public int getFieldWidth() {
                return VIRTUAL_WIDTH;
            }

            @Override
            public int getFieldHeight() {
                return VIRTUAL_HEIGHT;
            }

            @Override
            public double getDifferenceTime() {
                return 10.0;
            }
            @Override
            public List<Shape> getBorders() {
                return  borders;
            }
            @Override
            public List<Projectile> getProjectiles() {
                return  projectiles;
            }
        };

        robot.update(context);

        if (!projectiles.isEmpty()) {
            Iterator<Projectile> iter = projectiles.iterator();
            while (iter.hasNext()) {
                Projectile pjt = iter.next();
                if (pjt.isEnd()){

                    synchronized (projectiles) {
                        iter.remove();
                    }
                }
                pjt.update(context);
            }
        }
        if (enemies != null) {
            Iterator<Enemy> iter = enemies.iterator();
            while (iter.hasNext()) {
                Enemy enemy = iter.next();
                if (enemy.isEnd()){
                    synchronized (enemies) {
                        iter.remove();
                    }
                }
                enemy.update(context);
            }
        }

        double robotX = robot.getX();
        double robotY = robot.getY();
    }


    private void mouseClick(Point screenPoint) {
        double scaleX = (double) getWidth() / VIRTUAL_WIDTH;
        double scaleY = (double) getHeight() / VIRTUAL_HEIGHT;

        double offsetX = (getWidth() - VIRTUAL_WIDTH * scaleX) / 2;
        double offsetY = (getHeight() - VIRTUAL_HEIGHT * scaleY) / 2;

        int virtualX = (int) ((screenPoint.x - offsetX) / scaleX);
        int virtualY = (int) ((screenPoint.y - offsetY) / scaleY);

        this.mouseTarget = new Point(virtualX, virtualY);
        //robot.setTargetPosition(mouseTarget);
    }

//------------------------------Графические_Методы------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        double scaleX = (double) getWidth() / VIRTUAL_WIDTH;
        double scaleY = (double) getHeight() / VIRTUAL_HEIGHT;
        double scale = Math.min(scaleX, scaleY);

        double offsetX = (getWidth() - VIRTUAL_WIDTH * scale) / 2;
        double offsetY = (getHeight() - VIRTUAL_HEIGHT * scale) / 2;

        AffineTransform oldTransform = g2d.getTransform();

        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        drawObstacle(g2d);

        if (robot != null) {
            robot.draw(g2d);
        }
        if (!enemies.isEmpty()) {
            for (Enemy enemy : enemies) {
                enemy.draw(g2d);
            }
        }
        if (!projectiles.isEmpty()) {
            for (Projectile pjt : projectiles) {
                pjt.draw(g2d);
            }
        }
        g2d.setTransform(oldTransform);
    }

    private void drawObstacle(Graphics2D g) {
        g.setColor(Color.BLACK);
        for (Shape s : borders) {
            g.fill(s);
        }
    }

//------------------------------Внешние_Методы------------------------------

    public void spawnMob(Enemy enemy) {
        synchronized (enemies) {
            enemies.add(enemy);
        }
    }

//------------------------------Вспомогательные_Методы------------------------------
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

}
