package gui;


import api.GameContext;
import api.GameModel;
import api.ModelListener;
import log.Logger;
import objects.Enemies.Enemy;
import objects.Projectile.Projectile;

import java.awt.*;
import java.awt.geom.AffineTransform;

import java.util.*;
import java.util.List;


import javax.swing.*;

public class GameVisualizer extends JPanel implements ModelListener
{

    private final static int VIRTUAL_WIDTH = 600;
    private final static int VIRTUAL_HEIGHT = 600;

    private static Graphics g;
    private GameContext m_context;
//    private int m_currentOper;
    private GameModel m_gameModel;

    public GameVisualizer(GameModel gameModel)
    {
        m_gameModel = gameModel;
        setDoubleBuffered(true);
    }

    @Override
    public void modelUpdated(GameContext context) {
        //0 - all, 1 - robot, 2 - projectiles, 3 - enemies
        m_context = context;
        repaint();
    }


//------------------------------Графические_Методы------------------------------

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

        m_gameModel.getRobot().draw(g2d);

        if (m_context != null) {

            if (!m_context.getEnemies().isEmpty()) {
                for (Enemy enemy : m_context.getEnemies()) {
                    enemy.draw(g2d);
                }
            }
            if (!m_context.getProjectiles().isEmpty()) {
                for (Projectile pjt : m_context.getProjectiles()) {
                    pjt.draw(g2d);
                }
            }
        }
        g2d.setTransform(oldTransform);
    }

    private void drawObstacle(Graphics2D g) {
        g.setColor(Color.BLACK);
        List<Shape> borders;
        if (m_context == null) {
            borders = m_gameModel.getBorders();
        } else {
            borders = m_context.getBorders();
        }
        for (Shape s : borders) {
            g.fill(s);
        }
    }

}
