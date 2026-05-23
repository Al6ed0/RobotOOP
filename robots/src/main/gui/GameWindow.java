package gui;

import api.GameManager;

import java.awt.*;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends BaseInternalFrame
{
    private final GameManager m_manager;
    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        m_manager = new GameManager();
        JPanel panel = new JPanel(new CardLayout());
        panel.add(m_manager.getGameVisualizer(), "Game");
        getContentPane().add(panel);
        pack();
    }

    public GameManager getManager() {
        return m_manager;
    }
}
