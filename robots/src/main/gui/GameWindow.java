package gui;

import java.awt.*;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends BaseInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new CardLayout());
        panel.add(m_visualizer, "Game");
        getContentPane().add(panel);
        pack();
    }

    public GameVisualizer getVisualiser() {
        return m_visualizer;
    }
}
