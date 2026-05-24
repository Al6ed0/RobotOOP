package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.Properties;

import javax.swing.*;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

import log.Logger;
import objects.Enemies.ShooterDummy;
import objects.Enemies.SimpleDummy;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private GameWindow gameWindow;
    private LogWindow logWindow;
    private final File profileFile = new File(System.getProperty("user.home"), "profile.conf");

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        
        this.logWindow = createLogWindow();
        addWindow(logWindow);

        this.gameWindow = new GameWindow();
        gameWindow.setSize(600,  600);
        addWindow(gameWindow);

        if (profileFile.exists()) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Найден сохранённый профиль.\nВосстановить?",
                    "Восстановление", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                loadProfile();
            }
        }

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void confirmExit() {
        int response = JOptionPane.showConfirmDialog(this,
                "Вы действительно хотите выйти?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            saveProfile();
            System.exit(0);
        }
    }


    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
//        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
//        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }

    // Меню стиля

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = createSystemLookItem();
        JMenuItem crossplatformLookAndFeel = createCrossplatformLookItem();

        lookAndFeelMenu.add(systemLookAndFeel);
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookItem() {
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
    }

    private JMenuItem createCrossplatformLookItem() {
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndFeel;
    }

    // Меню Тестов

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = createLogTestItem();
        JMenuItem summonEnemyItem = createSimpleSummonItem();
        JMenuItem summonShooterEnemyItem = createShooterSummonItem();
        testMenu.add(addLogMessageItem);
        testMenu.add(summonEnemyItem);
        testMenu.add(summonShooterEnemyItem);

        return testMenu;
    }

    private JMenuItem createLogTestItem() {
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return addLogMessageItem;
    }
    private JMenuItem createSimpleSummonItem() {
        JMenuItem summonMenuItem = new JMenuItem("Создать болванчика");
        summonMenuItem.addActionListener((event) -> {
            gameWindow.getManager().spawnMob(new SimpleDummy(300, 100, 0.02 ));
        });
        return summonMenuItem;
    }
    private JMenuItem createShooterSummonItem() {
        JMenuItem summonMenuItem = new JMenuItem("Создать стрелка");
        summonMenuItem.addActionListener((event) -> {
            gameWindow.getManager().spawnMob(new ShooterDummy(300, 100, 0.01));
        });
        return summonMenuItem;
    }

    // Создание Меню

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu lookAndFeelMenu = createLookAndFeelMenu();
        JMenu testMenu = createTestMenu();
        JMenuItem toolkitMenu = createToolkitMenu();

        menuBar.add(toolkitMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private JMenu createToolkitMenu() {
        JMenu toolkitMenu = new JMenu("Приложение");
        toolkitMenu.setMnemonic(KeyEvent.VK_T);
        JMenuItem exitMenuItem = createExitMenuItem();

        toolkitMenu.add(exitMenuItem);

        return toolkitMenu;
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Выйти");

        exitMenuItem.addActionListener((event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING)
            );
        });
        return exitMenuItem;
    }

    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private void loadProfile() {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(profileFile)) {
            props.load(is);
            setBounds(
                    Integer.parseInt(props.getProperty("main.x")),
                    Integer.parseInt(props.getProperty("main.y")),
                    Integer.parseInt(props.getProperty("main.w")),
                    Integer.parseInt(props.getProperty("main.h"))
            );
            setExtendedState(Integer.parseInt(props.getProperty("main.state")));
            restoreInternalFrame(props, "game", gameWindow);
            restoreInternalFrame(props, "log", logWindow);
        } catch (IOException | PropertyVetoException e) {
            Logger.error("Возникла ошибка при загрузке профиля " + e.getMessage());
        }
    }

    private void restoreInternalFrame(Properties props, String prefix, JInternalFrame frame)
            throws PropertyVetoException {
        if (frame != null && props.containsKey(prefix + ".x")) {
            frame.setBounds(
                    Integer.parseInt(props.getProperty(prefix + ".x")),
                    Integer.parseInt(props.getProperty(prefix + ".y")),
                    Integer.parseInt(props.getProperty(prefix + ".w")),
                    Integer.parseInt(props.getProperty(prefix + ".h"))
            );
            frame.setVisible(Boolean.parseBoolean(props.getProperty(prefix + ".visible", "true")));
            boolean isMaximum = Boolean.parseBoolean(props.getProperty(prefix + ".max", "false"));

            if (isMaximum) {
                frame.setMaximum(true);
                frame.setIcon(Boolean.parseBoolean(props.getProperty(prefix + ".icon")));
            } else {
                frame.setIcon(Boolean.parseBoolean(props.getProperty(prefix + ".icon")));
            }
        }
    }

    private void saveProfile() {
        Properties props = new Properties();

        props.setProperty("main.x", String.valueOf(getX()));
        props.setProperty("main.y", String.valueOf(getY()));
        props.setProperty("main.w", String.valueOf(getWidth()));
        props.setProperty("main.h", String.valueOf(getHeight()));
        props.setProperty("main.state", String.valueOf(getExtendedState()));

        saveInternalFrame(props, "game", gameWindow);

        saveInternalFrame(props, "log", logWindow);

        try (OutputStream out = new FileOutputStream(profileFile)) {
            props.store(out, "Robot App Profile");
        } catch (IOException e) {
            Logger.error("Возникла ошибка при сохранении профиля " + e.getMessage());
        }
    }

    private void saveInternalFrame(Properties props, String prefix, JInternalFrame frame) {
        if (frame != null) {
            props.setProperty(prefix + ".max", String.valueOf(frame.isMaximum()));
            props.setProperty(prefix + ".visible", String.valueOf(frame.isVisible()));
            props.setProperty(prefix + ".icon", String.valueOf(frame.isIcon()));

            Rectangle bounds;
            if (frame instanceof BaseInternalFrame) {
                bounds = ((BaseInternalFrame) frame).getNormalBounds();
            } else {
                bounds = frame.getBounds();
            }
            props.setProperty(prefix + ".x", String.valueOf(bounds.x));
            props.setProperty(prefix + ".y", String.valueOf(bounds.y));
            props.setProperty(prefix + ".w", String.valueOf(bounds.width));
            props.setProperty(prefix + ".h", String.valueOf(bounds.height));
        }
    }
}
