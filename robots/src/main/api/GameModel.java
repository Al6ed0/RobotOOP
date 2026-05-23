package api;

import objects.ControlledEntity.ControlledEntity;
import objects.ControlledEntity.RobotVI;
import objects.Enemies.Enemy;
import objects.Projectile.Projectile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

public class GameModel implements KeyListener {
    private List<Shape> borders;
    private ControlledEntity robot = new RobotVI(300, 500);

    private final static int VIRTUAL_WIDTH = 600;
    private final static int VIRTUAL_HEIGHT = 600;

    private Point mouseTarget = null;

    private final List<ModelListener> listeners = new ArrayList<>();

    private List<Projectile> projectiles = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();

    private final Set<Integer> pressedKeys = new HashSet<>();

    public GameModel() {
        setupBorders();
    }

    private void setupBorders() {
        this.borders = new ArrayList<>();
        borders.add(new Rectangle(0, 0, 600, 10));
        borders.add(new Rectangle(0, 590, 600, 10));
        borders.add(new Rectangle(590, 0, 10, 600));
        borders.add(new Rectangle(0, 0, 10, 600));
    }

    protected void onUpdateEvent() {
        if (robot == null) {
            return;
        }
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
                return borders;
            }

            @Override
            public List<Projectile> getProjectiles() {
                return projectiles;
            }

            @Override
            public List<Enemy> getEnemies() {
                return enemies;
            }
        };
        double robotX = robot.getX();
        double robotY = robot.getY();

        robot.update(context);
//        int operation = 0;
//        if (robotX != robot.getX() || robotY != robot.getY()) {
//            operation = 1;
//        }

        if (!projectiles.isEmpty()) {
            Iterator<Projectile> iter = projectiles.iterator();
            while (iter.hasNext()) {
                Projectile pjt = iter.next();
                if (pjt.isEnd()) {

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
                if (enemy.isEnd()) {
                    synchronized (enemies) {
                        iter.remove();
                    }
                }
                enemy.update(context);
            }
        }
        notifyListeners(context);

    }

    public void bulletHell() {
        synchronized (projectiles) {
            projectiles.addAll(robot.shoot());
            for (Enemy enemy : enemies) {
                projectiles.addAll(enemy.attack());
            }
        }
    }

//------------------------------Observer_Pattern_Methods------------------------------

    public void addListener(ModelListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(GameContext context) {
        for (ModelListener listener : listeners) {
            listener.modelUpdated(context);
        }
    }

//------------------------------Внешние_Методы------------------------------

    public void spawnMob(Enemy enemy) {
        synchronized (enemies) {
            enemies.add(enemy);
        }
    }

//------------------------------Key_Listener_Methods------------------------------
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D
                    -> pressedKeys.add(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D
                    -> pressedKeys.remove(e.getKeyCode());
        }
    }
//------------------------------GET_Methods------------------------------
    public List<Shape> getBorders() {
        return this.borders;
    }
    public ControlledEntity getRobot() {
        return robot;
    }
}
