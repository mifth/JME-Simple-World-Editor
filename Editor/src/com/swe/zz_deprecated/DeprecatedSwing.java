package com.swe.zz_deprecated; 

import com.swe.scene.EditorSceneFilter;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;
import com.swe.EditorBaseManager;
import com.swe.EditorTextureFilter;
import com.swe.SimpleEditor;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class DeprecatedSwing {
    
    
    protected static JFileChooser mFileCm;
    protected static FileFilter modFilter = new EditorSceneFilter();
    protected static FileFilter texFilter = new EditorTextureFilter();
    protected JList listEntity, listGeo;
    protected DefaultListModel modelEntity, modelGeo;
    protected ArrayList<String> strLst = new ArrayList<String>();
    
    private static JmeCanvasContext context;
    protected static Canvas canvas;
    protected static JFrame frame;
    protected static Container canvasPanel1, canvasPanel2, canvasPanelOpt1, canvasPanelOpt2;
    protected  static JScrollPane logScrollPane;
    private static Container currentPanel;
    protected static JTabbedPane tabbedPane, tabbedPaneOpt;
    private static final String appClass = SimpleEditor.class.getName();
    protected static JMenu menuTortureMethods;    
    protected static JTextArea LogArea;

    protected static Application app;    
    protected static AppSettings settings;
    private static EditorBaseManager baseParts;
    
    public DeprecatedSwing() {
        
            //Create a file chooser for Models
        mFileCm = new JFileChooser();
        //mFileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mFileCm.addChoosableFileFilter(modFilter);
        mFileCm.addChoosableFileFilter(texFilter);
        mFileCm.setAcceptAllFileFilterUsed(false);
        mFileCm.setPreferredSize(new Dimension(800, 600));
        
    }



    private static void createTabs(){
        tabbedPane = new JTabbedPane();
        
        canvasPanel1 = new JPanel();
        canvasPanel1.setLayout(new BorderLayout());
        tabbedPane.addTab("Editor", canvasPanel1);
        
        
        
        canvasPanel2 = new Container();
        canvasPanel2.setLayout(new BorderLayout());
        tabbedPane.addTab("Log", canvasPanel2);

        
        
        // Create the log area
        LogArea = new JTextArea(5,20);
        LogArea.setMargin(new Insets(5,5,5,5));
        LogArea.setEditable(false);
        LogArea.setBackground(Color.LIGHT_GRAY);
        logScrollPane = new JScrollPane(LogArea);
//        logScrollPane.setPreferredSize(new Dimension(1000, 700));
        canvasPanel2.add(logScrollPane);
        
        PrintStream consoleStream = new PrintStream(new OutputStream() {

            @Override
            public void write(int i) throws IOException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            public void write(byte[] b, int off, int len) throws IOException {
            LogArea.append(new String(b, off, len));
            }
            
        });
        
        System.setOut(consoleStream);
        System.setErr(consoleStream);
        
//        frame.getContentPane().add(tabbedPane);
        
        currentPanel = canvasPanel1;
        
        
        // Create the options and properties panel
        tabbedPaneOpt = new JTabbedPane();
        tabbedPaneOpt.setMinimumSize(new Dimension(201,31));
        tabbedPaneOpt.setPreferredSize(new Dimension(210,40));
        canvasPanelOpt1 = new JPanel();
        canvasPanelOpt1.setLayout(new FlowLayout());
        canvasPanelOpt1.setMinimumSize(new Dimension(200,30));
        canvasPanelOpt1.setPreferredSize(new Dimension(210,40)); 
        tabbedPaneOpt.addTab("Settings", canvasPanelOpt1);         
//        canvasPanelOpt1.setSize(200, 300);

        canvasPanelOpt2 = new JPanel();
        canvasPanelOpt2.setLayout(new FlowLayout());
        canvasPanelOpt2.setMinimumSize(new Dimension(200,30));	
        canvasPanelOpt2.setPreferredSize(new Dimension(210,40));
        tabbedPaneOpt.addTab("Shaders", canvasPanelOpt2);        
        
        
        
        // optionPanel and canvasPanel Splitting
        JSplitPane split = new JSplitPane();
        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(tabbedPane);
        split.setRightComponent(tabbedPaneOpt);
        split.setResizeWeight(1);
                
        frame.getContentPane().add(split, BorderLayout.CENTER); 
        
        
        
    }
    
    private static void createMenu(){
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // create menu
        menuTortureMethods = new JMenu("File");
        menuBar.add(menuTortureMethods);
        
    }
    
    
    
    private static void createFrame(){
        frame = new JFrame("simpleEditor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                app.stop();
            }
        });

        createTabs();
        createMenu();
        
        
        // add menu items
        EditorMenuItems mItems = new EditorMenuItems();
        mItems.menuButtonz();
        mItems.panelButtonz(canvasPanelOpt1);

//        baseParts = new EditorBaseParts(app);
       
    }

    public static void createCanvas(String appClass){
        settings = new AppSettings(true);
	
        // Get the default toolkit
	Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        // Get the current screen size
	Dimension scrnsize = toolkit.getScreenSize();
        
        settings.setVSync(true);
        settings.setWidth(scrnsize.width - 400);
        settings.setHeight(scrnsize.height - 300);

        try{
            Class<? extends Application> clazz = (Class<? extends Application>) Class.forName(appClass);
            app = clazz.newInstance();
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }catch (InstantiationException ex){
            ex.printStackTrace();
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }

//        app = new Application();
        app.setPauseOnLostFocus(false);
        app.setSettings(settings);
        app.createCanvas();
        app.startCanvas();
//        app.setPauseOnLostFocus(true);
        
        context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());
        
    }

    public static void startApp(){
        app.startCanvas();
        app.enqueue(new Callable<Void>(){
            public Void call(){
                if (app instanceof SimpleApplication){
                    SimpleApplication simpleApp = (SimpleApplication) app;
                    simpleApp.getFlyByCamera().setDragToRotate(true);
//                    simpleApp.getStateManager().getState(StatsAppState.class).setDisplayStatView(true);
//                    app.getStateManager().getState(StatsAppState.class).setDisplayStatView(true);
                }
                return null;
            }
        });
        
    }

    public static void main(String[] args){
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
        
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
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
