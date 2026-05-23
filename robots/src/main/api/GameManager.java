package api;


import gui.GameVisualizer;
import objects.Enemies.Enemy;

import java.awt.*;

import java.util.*;
import java.util.Timer;
import javax.swing.*;

public class GameManager {
    private final Timer m_timer = initTimer();


    private static final GameModel gameModel = new GameModel();
    private static final GameVisualizer gameVisualizer = new GameVisualizer(gameModel);


    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameManager() {
//        m_timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                EventQueue.invokeLater(gameVisualizer::repaint);
//            }
//        }, 0, 16);

        gameVisualizer.addKeyListener(gameModel);
        gameVisualizer.setFocusable(true);
        gameVisualizer.requestFocusInWindow();

        gameModel.addListener(gameVisualizer);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameModel.onUpdateEvent();
            }
        }, 0, 10);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameModel.bulletHell();
            }
        }, 0, 1000);

    }

//    private void setupKeyBinds() {
//        int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D};
//
//        for (int keyCode : keys) {
//            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
//                    KeyStroke.getKeyStroke(keyCode, 0, false),
//                    "press" + keyCode);
//            getActionMap().put("press" + keyCode, new AbstractAction() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    pressedKeys.add(keyCode);
//                }
//            });
//
//            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
//                    KeyStroke.getKeyStroke(keyCode, 0, true),
//                    "release" + keyCode
//            );
//            getActionMap().put("release" + keyCode, new AbstractAction() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    pressedKeys.remove(keyCode);
//                }
//            });
//        }
//    }


    public GameVisualizer getGameVisualizer() {
        return gameVisualizer;
    }
//------------------------------Внешние_Методы------------------------------

    public void spawnMob(Enemy enemy) {
        gameModel.spawnMob(enemy);
    }


}