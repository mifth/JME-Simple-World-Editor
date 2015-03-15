package com.swe;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.swing.*;

public class SimpleEditorSwing {

    private static JmeCanvasContext context;
    protected static Canvas canvas;
    protected static JFrame frame;
    protected static Container canvasPanel1, canvasPanel2, canvasPanelOpt1, canvasPanelOpt2;
    protected static JScrollPane logScrollPane;
    private static Container currentPanel;
    protected static JTabbedPane tabbedPane, tabbedPaneOpt;
    private static final String appClass = SimpleEditor.class.getName();
    protected static JMenu menuTortureMethods;
    protected static JTextArea LogArea;
    protected static Application app;
    protected static AppSettings settings;

    private static void createTabs() {
//        tabbedPane = new JTabbedPane();

        canvasPanel1 = new JPanel();
        canvasPanel1.setLayout(new BorderLayout());


        frame.getContentPane().add(canvasPanel1);

        currentPanel = canvasPanel1;

    }

    private static void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

//        // create menu
        menuTortureMethods = new JMenu("File");
        menuBar.add(menuTortureMethods);

    }

    private static void createFrame() {
        frame = new JFrame("SimpleWorldEditor " + EditorVersion.editorVersion);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                app.stop();
            }
        });

        createTabs();

    }

    public static void createCanvas(String appClass) {
        settings = new AppSettings(true);

        // Get the default toolkit
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // Get the current screen size
        Dimension scrnsize = toolkit.getScreenSize();

//        settings.setVSync(true);
        settings.setWidth(scrnsize.width - 100);
        settings.setHeight(scrnsize.height - 100);

        try {
            Class<? extends Application> clazz = (Class<? extends Application>) Class.forName(appClass);
            app = clazz.newInstance();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

//        app = new Application();
        app.setPauseOnLostFocus(true);
        app.setSettings(settings);
        app.createCanvas();
        app.startCanvas();
//        app.setPauseOnLostFocus(true);

        context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());

    }

    public static void startApp() {
        app.startCanvas();
        app.enqueue(new Callable<Void>() {
            public Void call() {
                if (app instanceof SimpleApplication) {
                    SimpleApplication simpleApp = (SimpleApplication) app;
//                    simpleApp.getFlyByCamera().setDragToRotate(true);
                }
                return null;
            }
        });

    }

    public static void main(String[] args) {
        JmeFormatter formatter = new JmeFormatter();

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);

        Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
        Logger.getLogger("").addHandler(consoleHandler);

        createCanvas(appClass);

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

//                try {
//
//                    String strLook = null;
//                    if (System.getProperty("os.name").equals("Linux")) {
//                        strLook = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
//                    } else {
//                        strLook = UIManager.getSystemLookAndFeelClassName();
//                    }
//
//                    UIManager.setLookAndFeel(strLook);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }

                JPopupMenu.setDefaultLightWeightPopupEnabled(false);

                createFrame();

                currentPanel.add(canvas, BorderLayout.CENTER);
                frame.pack();
                startApp();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
