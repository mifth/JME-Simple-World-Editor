/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe;

import com.swe.events.EditorMappings;
import com.swe.camera.EditorCameraManager;
import com.swe.scene.EditorLayersGroupObject;
import com.swe.managers.EditorDataManager;
import com.swe.selection.EditorSelectionManager;
import com.swe.history.EditorHistoryManager;
import com.swe.scene.EditorSceneManager;
import com.swe.gui.EditorGuiManager;
import com.swe.transform.EditorTransformManager;
import com.swe.es.EntityManager;
import com.swe.es.EntitySpatialsSystem;
import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.swe.events.EditorEventManager;

/**
 *
 * @author mifth
 */
public class EditorBaseManager {

    private Application app;
    private AssetManager assetManager;
    private Camera sceneCamera;
    private ViewPort viewPort;
    private FlyByCamera flyCam;
    
    // Global Nodes
    private Node rootNode, guiNode;
    private Node camTrackHelper;
    
    // Tools
    private EditorCameraManager camManager;
    private EditorTransformManager transformManager;
    private EditorMappings mappings;
    private EditorSelectionManager selectionManager;
    private EditorLayersGroupObject layerManager;
    private EntityManager entityManager;
    private EntitySpatialsSystem spatialSystem;
    private EditorSceneManager sceneManager;
    private EditorGuiManager gui;
    private EditorDataManager dataManager;
    private EditorHistoryManager historyManager;
    private EditorEventManager eventManager;

    // Version of the Editor
    private static float editorVersion;

    
   private EditorBaseManager() {}
 
   /**
    * SingletonHolder is loaded on the first execution of Singleton.getInstance()
    * or the first access to SingletonHolder.INSTANCE, not before.
    */
   private static class SingletonHolder {
     private static final EditorBaseManager INSTANCE = new EditorBaseManager();
   }
 
   public static EditorBaseManager getInstance() {
     return SingletonHolder.INSTANCE;
   }
    
    protected void setApp(Application app) {

        this.app = app;
        sceneCamera = this.app.getCamera();
        viewPort = this.app.getViewPort();
        assetManager = this.app.getAssetManager();
        
        editorVersion = EditorVersion.editorVersion;

        flyCam = this.app.getStateManager().getState(FlyCamAppState.class).getCamera();
        flyCam.setEnabled(false);

        setGlobalNodes();
        
        camManager = new EditorCameraManager(this.app, this);        
        camManager.setCamTracker();
//        app.getStateManager().attach(camManager);
        mappings = new EditorMappings(this.app, this);


//        Node tempNode = new Node();
//        rootNode.attachChild(tempNode);
        // setup global tools
        eventManager = new EditorEventManager(this);
        app.getStateManager().attach(eventManager);
        
        historyManager = new EditorHistoryManager(this.app, this);
        dataManager = new EditorDataManager();
        selectionManager = new EditorSelectionManager(this.app, this);
        this.app.getStateManager().attach(selectionManager);
        transformManager = new EditorTransformManager(this.app, this);
        this.app.getStateManager().attach(transformManager);   
        spatialSystem = new EntitySpatialsSystem();
        entityManager = new EntityManager();
        sceneManager = new EditorSceneManager(this.app, this);

//        setSomeEntities();

        gui = new EditorGuiManager(this);
        this.app.getStateManager().attach(gui);        
        
    }

    private void setGlobalNodes() {

        rootNode = (Node) app.getViewPort().getScenes().get(0);
        guiNode = (Node) app.getGuiViewPort().getScenes().get(0);        
        
        camTrackHelper = new Node("camTrackHelper");
        rootNode.attachChild(camTrackHelper);                
       
    }

    public float getEditorVersion() {
        return editorVersion;
    }    
    
    public EditorCameraManager getCamManager() {
        return camManager;
    }    
    
    public EditorTransformManager getTransformManager() {
        return transformManager;
    }

    public EditorSelectionManager getSelectionManager() {
        return selectionManager;
    }    
    
    public EditorMappings getEditorMappings() {
        return mappings;
    }    
    
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public EntitySpatialsSystem getSpatialSystem() {
        return spatialSystem;
    }

//    public EditorSceneLayers getLayerManager() {
//        return layerManager;
//    }    
    
    public EditorSceneManager getSceneManager() {
        return sceneManager;
    }

    public EditorGuiManager getGuiManager() {
        return gui;
    }
    
    public EditorDataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(EditorDataManager dataManager) {
        this.dataManager = dataManager;
    }    
    
    public EditorHistoryManager getHistoryManager() {
        return historyManager;
    }    

    public EditorEventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EditorEventManager eventManager) {
        this.eventManager = eventManager;
    }
}
