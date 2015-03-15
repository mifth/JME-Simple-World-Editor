package com.swe;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

public class SimpleEditor extends SimpleApplication {

    public static void main(String[] args) {
        SimpleEditor app = new SimpleEditor();
        AppSettings aps = new AppSettings(true);
        aps.setVSync(true);
//        aps.setFrameRate(80);
//        aps.setResolution(1600, 800);
        app.setSettings(aps);
        app.setShowSettings(false);
        app.start();
    }
    
    private EditorUpdateManager updateManager;

    @Override
    public void simpleInitApp() {

//        this.setShowSettings(false);
        this.setDisplayStatView(false);
        EditorBaseManager baseManager = EditorBaseManager.getInstance();
        baseManager.setApp(this);
        
        updateManager = new EditorUpdateManager(baseManager.getCamManager(),
                baseManager.getTransformManager());

    }

    @Override
    public void simpleUpdate(float tpf) {
        updateManager.doUpdate();

    }
}
