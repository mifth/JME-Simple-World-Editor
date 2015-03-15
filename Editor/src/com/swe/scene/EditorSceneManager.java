/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swe.es.ComponentsControl;
import com.swe.es.components.EntityModelPathComponent;
import com.swe.es.components.EntityNameComponent;
import com.swe.es.EntitySpatialsControl;
import com.jme3.app.Application;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.swe.EditorBaseManager;
import de.lessvoid.nifty.controls.RadioButton;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author mifth
 */
public class EditorSceneManager {

    private AssetManager assetMan;
    private Node root, guiNode;
    private Application app;
    private EditorBaseManager base;
//    private final JFileChooser mFileCm;
//    private FileFilter modFilter;
    private static List<String> assetsList;
    private static ConcurrentHashMap<String, EditorSceneObject> scenesList;
    private EditorSceneObject activeScene;
    private static ConcurrentHashMap<String, String> entitiesList;
    private static ConcurrentHashMap<String, Spatial> spatialsList;
    private DesktopAssetManager dsk;
    private String scenePathCache, sceneNameCache;
    private boolean savePreviewJ3o;
    private DirectionalLight dl;
    private AmbientLight al;
    private Node notFoundModel;

    public EditorSceneManager(Application app, EditorBaseManager base) {

        this.app = app;
        this.base = base;
        assetMan = this.app.getAssetManager();
        root = (Node) this.app.getViewPort().getScenes().get(0);
        guiNode = (Node) this.app.getGuiViewPort().getScenes().get(0);

//        mFileCm = new JFileChooser();
//        mFileCm.addChoosableFileFilter(modFilter);
//        mFileCm.setAcceptAllFileFilterUsed(false);
//        mFileCm.setPreferredSize(new Dimension(800, 600));
//        modFilter = new EditorSceneFilter();

        scenePathCache = null;
        sceneNameCache = null;


        assetsList = new ArrayList<String>();
        scenesList = new ConcurrentHashMap<String, EditorSceneObject>();
        entitiesList = new ConcurrentHashMap<String, String>();
        spatialsList = new ConcurrentHashMap<String, Spatial>();
        dsk = (DesktopAssetManager) assetMan;

        initializeTempLighting();

        app.getViewPort().setBackgroundColor(ColorRGBA.DarkGray);

        savePreviewJ3o = false;

        // create new scene
        EditorSceneObject scene1 = createSceneObject("Scene1");
        scene1.createLayersGroup("LayersGroup1");
        scene1.setActivelayersGroup(scene1.getLayersGroupsList().get("LayersGroup1"));

        EditorSceneObject scene2 = createSceneObject("Scene2");
        setActiveSceneObject(scene2);
        scene2.createLayersGroup("LayersGroup1");
        scene2.createLayersGroup("LayersGroup2");
        scene2.setActivelayersGroup(scene2.getLayersGroupsList().get("LayersGroup1"));
        getActiveSceneObject().getActivelayersGroup().enableLayer(1); // set 1 layer enabled

        // Unfound Model
        notFoundModel = new Node("NOTFOUNDMODEL");
        Geometry notFoundGeom = new Geometry("NOTFOUNDMODEL", new Box(1f, 1f, 1f));
        Material notFoundMat = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        notFoundMat.setColor("Color", ColorRGBA.Red);
        notFoundGeom.setMaterial(notFoundMat);
        notFoundModel.attachChild(notFoundGeom);

    }

    public boolean loadScene() {
        Window wind = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        FileDialog fd = new FileDialog((Dialog) wind, "Choose a file", FileDialog.LOAD);
        fd.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".swe")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        fd.setAutoRequestFocus(true);
//        fd.setAlwaysOnTop(true);
        fd.setVisible(true);

//        mFileCm.setDialogType(JFileChooser.OPEN_DIALOG);
//        mFileCm.setDialogTitle("Load Scene");
//        mFileCm.setApproveButtonToolTipText("Open");
//        mFileCm.setApproveButtonText("Open");
//        mFileCm.setFileFilter(modFilter);
//        int returnVal = mFileCm.showOpenDialog(null);
//
        if (fd.getDirectory() != null && fd.getFile() != null && fd.getFile().endsWith(".swe")) {
            File selectedFile = new File(correctPath(fd.getDirectory() + fd.getFile()));

            if (selectedFile.exists() && selectedFile.isFile() && selectedFile.getName().indexOf(".") != 0 && selectedFile.getName().length() > 0) {
                String filePath = selectedFile.getParent();
                filePath = correctPath(filePath);

                String fileName = selectedFile.getName();
                if (fileName.indexOf(".") > 0) {
                    fileName = fileName.substring(0, fileName.indexOf("."));
                }
                String fullPath = filePath + fileName;

                newScene();

                // set scene paths to cache
                sceneNameCache = fileName;
                scenePathCache = filePath;

                loadSweFile(fullPath);

                fd.dispose();
                return true;
            }
        }

        fd.dispose();
        return false;
    }

    protected void loadSettings(JsonObject jsSettings) {

        // set new IDX
        long lastIDX = jsSettings.get("LastIDX").getAsLong();
        base.getEntityManager().setIdx(lastIDX);

        // version of the editor which the scene was saved
        // Not Used Yet
        //String SWEVersion = (String) jsScene.get("EditorVersion");

        // SevePreviewj3O
        savePreviewJ3o = jsSettings.get("savePreviewJ3o").getAsBoolean();

        // load assets
        JsonObject jsPaths = jsSettings.get("AssetsPaths").getAsJsonObject();
        for (Map.Entry<String, JsonElement> obj : jsPaths.entrySet()) {
            System.out.println("Loaded Path: " + obj.getValue().getAsString());
            addAsset(obj.getValue().getAsString());
        }


        JsonObject constraintsJson = jsSettings.get("TransformConstraints").getAsJsonObject();

        // MOVE CONSTR
        float moveConstrValue = constraintsJson.get("MoveConstraint").getAsFloat();
        base.getTransformManager().getConstraintTool().setMoveConstraint(moveConstrValue);

        String moveRadioStr = "move_constraint_none";
        if (moveConstrValue == 0.5f) {
            moveRadioStr = "move_constraint_0.5";
        } else if (moveConstrValue == 1f) {
            moveRadioStr = "move_constraint_1";
        } else if (moveConstrValue == 5f) {
            moveRadioStr = "move_constraint_5";
        } else if (moveConstrValue == 10f) {
            moveRadioStr = "move_constraint_10";
        } else if (moveConstrValue == 50f) {
            moveRadioStr = "move_constraint_50";
        }

        RadioButton moveConstrRadioButton = base.getGuiManager().getNifty().getCurrentScreen().findNiftyControl(moveRadioStr, RadioButton.class);
        moveConstrRadioButton.select();

        // ROTATE CONSTR
        float rotateConstrValue = constraintsJson.get("RotateConstraint").getAsFloat();
        base.getTransformManager().getConstraintTool().setRotateConstraint(rotateConstrValue);

        String rotateRadioStr = "rotate_constraint_none";
        if (rotateConstrValue == 1f) {
            rotateRadioStr = "rotate_constraint_1";
        } else if (rotateConstrValue == 5f) {
            rotateRadioStr = "rotate_constraint_5";
        } else if (rotateConstrValue == 10f) {
            rotateRadioStr = "rotate_constraint_10";
        } else if (rotateConstrValue == 45f) {
            rotateRadioStr = "rotate_constraint_45";
        }

        RadioButton rotateConstrRadioButton = base.getGuiManager().getNifty().getCurrentScreen().findNiftyControl(rotateRadioStr, RadioButton.class);
        rotateConstrRadioButton.select();

        // SCALE CONSTR
        float scaleConstrValue = constraintsJson.get("ScaleConstraint").getAsFloat();
        base.getTransformManager().getConstraintTool().setScaleConstraint(scaleConstrValue);

        String scaleRadioStr = "scale_constraint_none";
        if (scaleConstrValue == 0.1f) {
            scaleRadioStr = "scale_constraint_0.1";
        } else if (scaleConstrValue == 0.5f) {
            scaleRadioStr = "scale_constraint_0.5";
        }

        RadioButton scaleConstrRadioButton = base.getGuiManager().getNifty().getCurrentScreen().findNiftyControl(scaleRadioStr, RadioButton.class);
        scaleConstrRadioButton.select();


        System.out.println("Settings are loaded!");
    }

    protected void loadEntityFromJson(Node layerNode, JsonObject jsEntity) {

//                    System.out.println(objID + "OBJID");

        long ID = jsEntity.get("ID").getAsLong();

        String idPath = jsEntity.get("IDPath").getAsString();

        String idName = jsEntity.get("IDName").getAsString();
        idName = idName.substring(0, idName.indexOf("_IDX"));

        System.out.println("Name and Path: " + idName + "  " + idPath);

        // create entity
        long entID = createEntityModel(idName, idPath, ID);
        Node entityNode = (Node) base.getSpatialSystem().getSpatialControl(entID).getGeneralNode();


        //set Transform for the entity
        JsonObject jsTransform = (JsonObject) jsEntity.get("IDTransform");
        Transform entTransform = new Transform();
        entTransform.setTranslation(jsTransform.get("translationX").getAsFloat(), jsTransform.get("translationY").getAsFloat(),
                jsTransform.get("translationZ").getAsFloat());
        entTransform.setRotation(new Quaternion(
                jsTransform.get("rotationX").getAsFloat(), jsTransform.get("rotationY").getAsFloat(), jsTransform.get("rotationZ").getAsFloat(),
                jsTransform.get("rotationW").getAsFloat()));
        entTransform.setScale(jsTransform.get("scaleX").getAsFloat(), jsTransform.get("scaleY").getAsFloat(), jsTransform.get("scaleZ").getAsFloat());
        entityNode.setLocalTransform(entTransform);

        System.out.println(entTransform.toString());

        // Attach Entity
        layerNode.attachChild(entityNode);

        //set data components for the entity
        JsonObject jsDataComponents = (JsonObject) jsEntity.get("IDDataComponents");
        for (Map.Entry<String, JsonElement> strKey : jsDataComponents.entrySet()) {
            String value = strKey.getValue().getAsString();
            base.getDataManager().getEntityData(ID).put(strKey.getKey(), value);
        }

    }

    protected void loadSweFile(String filePath) {
        JsonObject loadSWEJson = loadToJsonFile(filePath + ".swe", false);

        loadSettings(loadSWEJson.getAsJsonObject("Settings"));

        // load Scenes
        for ( JsonElement sceneElement : loadSWEJson.get("Scenes").getAsJsonArray()) {

            JsonObject sceneJO = sceneElement.getAsJsonObject();

            // set new Scene
            String strScene = sceneJO.get("sceneName").getAsString();
            EditorSceneObject newSceneObj = new EditorSceneObject(root, strScene);
            newSceneObj.setSceneEnabled(sceneJO.get("isEnabled").getAsBoolean());

            // set Scene active
            Node newSceneNode = newSceneObj.getSceneNode();
            newSceneNode.setUserData("isActive", sceneJO.get("isActive").getAsBoolean());
            if (sceneJO.get("isActive").getAsBoolean()) {
                setActiveSceneObject(newSceneObj);
            }

            scenesList.put(strScene, newSceneObj); // add a scene to the list

            // Load LayersGroups of a Scene
            for (  JsonElement layersGroupElem : sceneJO.get("LayersGroups").getAsJsonArray()) {
                
                JsonObject layersGroupJO = layersGroupElem.getAsJsonObject();

                String layerGroupNameFromJO = layersGroupJO.get("layerGroupName").getAsString();
                newSceneObj.createLayersGroup(layerGroupNameFromJO);
                EditorLayersGroupObject newLayersGroup = newSceneObj.getLayersGroupsList().get(layerGroupNameFromJO);
                newLayersGroup.setLayersGroupEnabled(layersGroupJO.get("isEnabled").getAsBoolean());

                // set LayersGroup active
                Node newLayersGroupNode = newLayersGroup.getLayersGroupNode();
                if (layersGroupJO.get("isActive").getAsBoolean()) {
                    newSceneObj.setActivelayersGroup(newLayersGroup);
                }


                // load layers
                for ( JsonElement layerElem : layersGroupJO.get("Layers").getAsJsonArray()) {
                    JsonObject layerJO = layerElem.getAsJsonObject();
                    // get layer
                    int strLayer = layerJO.get("layerNumber").getAsInt();
                    Node layerNode = newLayersGroup.getLayer(strLayer);
//                    System.out.println(strLayer + "Layer number");

                    // get layer states
                    boolean isActive = layerJO.get("isActive").getAsBoolean();
                    if (isActive) {
                        newLayersGroup.setActiveLayer(layerNode);
//                    layerNode.setUserData("isActive", true);
                    }

                    // don't forget to parse gui layers
                    boolean isEnabled = layerJO.get("isEnabled").getAsBoolean();
                    if (isEnabled) {
                        newLayersGroup.getLayersGroupNode().attachChild(layerNode);
                        layerNode.setUserData("isEnabled", true);
                    }

                    // Locked Layer stats
                    boolean isLocked = layerJO.get("isLocked").getAsBoolean();
                    if (isLocked) {
                        layerNode.setUserData("isLocked", true);
                    }
                }
            }
        }

        // LOAD ENTITIES
        for (JsonElement entObj : loadSWEJson.getAsJsonArray("Entities")) {
            JsonObject jsEntity = entObj.getAsJsonObject();

            // PLACE OF ENTITY
            String entSceneName = entObj.getAsJsonObject().getAsJsonObject("IDScenePlace").get("Scene").getAsString();
            String layerGroupName = jsEntity.get("IDScenePlace").getAsJsonObject().get("LayerGroup").getAsString();
            int layerNumber = jsEntity.get("IDScenePlace").getAsJsonObject().get("Layer").getAsInt();

            Node layerNode = scenesList.get(entSceneName).getLayersGroupsList().get(layerGroupName).getLayer(layerNumber);

            loadEntityFromJson(layerNode, jsEntity);
        }


    }

    public void saveScene() {
        if (scenePathCache != null && sceneNameCache != null) {
            saveSweFile(scenePathCache + sceneNameCache);

            if (savePreviewJ3o) {
                savePreviewScene(scenePathCache, sceneNameCache);
            }
        } else {
            saveAsNewScene();
        }

    }

    public void saveAsNewScene() {

        Window wind = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        FileDialog fd = new FileDialog((Dialog) wind, "Choose a file", FileDialog.SAVE);
        fd.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".swe")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        fd.setAutoRequestFocus(true);
//        fd.setAlwaysOnTop(true);
        fd.setVisible(true);

//        mFileCm.setDialogType(JFileChooser.SAVE_DIALOG);
//        mFileCm.setDialogTitle("Save Scene");
//        mFileCm.setApproveButtonToolTipText("Save");
//        String s = "Save";
//        mFileCm.setApproveButtonText("Save");
//        mFileCm.setFileFilter(modFilter);
//        int returnVal = mFileCm.showOpenDialog(null);

        if (fd.getDirectory() != null && fd.getFile() != null) {

            String pathTosave = fd.getDirectory() + fd.getFile();
            if (!pathTosave.endsWith(".swe")) {
                pathTosave += ".swe";
            }

            pathTosave = correctPath(pathTosave);

            File selectedFile = new File(pathTosave);

            if (selectedFile.getName().indexOf(".") != 0 && selectedFile.getName().length() > 0) {
                String filePath = selectedFile.getParent();
                filePath = correctPath(filePath);

                String fileName = selectedFile.getName();
                if (fileName.indexOf(".") > 0) {
                    fileName = fileName.substring(0, fileName.indexOf("."));
                }
                String fullPath = filePath + fileName;

                // set paths to cache
                scenePathCache = filePath;
                sceneNameCache = fileName;

                saveSweFile(fullPath);

                if (savePreviewJ3o) {
                    savePreviewScene(filePath, fileName); // save j3o
                }
            }
        }

        fd.dispose();
    }

    private JsonObject saveSettings() {

        JsonObject settingsJson = new JsonObject();

        // save Last IDX
        settingsJson.addProperty("LastIDX", base.getEntityManager().getIdx());

        // save assets paths
        JsonObject assetsToSave = new JsonObject();
//        int assetIndex = 1;
        for (String str : assetsList) {

            assetsToSave.addProperty("AssetPath_" + assetsList.indexOf(str), str);
        }
        settingsJson.add("AssetsPaths", assetsToSave);

        // save version of the Simple World Editor
        settingsJson.addProperty("EditorVersion", base.getEditorVersion());
        settingsJson.addProperty("savePreviewJ3o", savePreviewJ3o);

        JsonObject constraintsJson = new JsonObject();
        constraintsJson.addProperty("MoveConstraint", base.getTransformManager().getConstraintTool().getMoveConstraint());
        constraintsJson.addProperty("RotateConstraint", base.getTransformManager().getConstraintTool().getRotateConstraint());
        constraintsJson.addProperty("ScaleConstraint", base.getTransformManager().getConstraintTool().getScaleConstraint());
        settingsJson.add("TransformConstraints", constraintsJson);


        return settingsJson;

    }

    private void saveSweFile(String pathToSave) {
        JsonObject saveSWEJson = new JsonObject();

        JsonObject settingsJson = saveSettings();
        saveSWEJson.add("Settings", settingsJson);

        //save scenes
        JsonArray allScenes = new JsonArray();

        // save entities
        JsonArray entitiesToSave = new JsonArray();

        for (String sceneName : scenesList.keySet()) {
            JsonObject sceneToSave = new JsonObject();

            // save scene states
            Node sceneNode = scenesList.get(sceneName).getSceneNode();
            sceneToSave.addProperty("sceneName", scenesList.get(sceneName).getSceneName());
            Object isActScene = sceneNode.getUserData("isActive");
            sceneToSave.addProperty("isActive", (Boolean) isActScene);
            Object isEnScene = sceneNode.getUserData("isEnabled");
            sceneToSave.addProperty("isEnabled", (Boolean) isEnScene);


            //save LayerGroups
            JsonArray allLayersGroups = new JsonArray();
            for (EditorLayersGroupObject layersGroup : scenesList.get(sceneName).getLayersGroupsList().values()) {
                JsonObject layerGroupToSave = new JsonObject();

                // save layerGroup states
                Node layersGroupNode = layersGroup.getLayersGroupNode();
                layerGroupToSave.addProperty("layerGroupName", layersGroup.getLayersGroupName());
                Object isActLayersGroup = layersGroupNode.getUserData("isActive");
                layerGroupToSave.addProperty("isActive", (Boolean) isActLayersGroup);
                Object isEnLayersGroup = layersGroupNode.getUserData("isEnabled");
                layerGroupToSave.addProperty("isEnabled", (Boolean) isEnLayersGroup);

                //save layers
                JsonArray allLayers = new JsonArray();
                for (Node layerNode : layersGroup.getLayers()) {
                    JsonObject layerToSave = new JsonObject();

                    // save layer states
                    layerToSave.addProperty("layerNumber", (Integer) layerNode.getUserData("LayerNumber"));
                    Object isActLayer = layerNode.getUserData("isActive");
                    layerToSave.addProperty("isActive", (Boolean) isActLayer);
                    Object isEnLayer = layerNode.getUserData("isEnabled");
                    layerToSave.addProperty("isEnabled", (Boolean) isEnLayer);
                    Object isLockedLayer = layerNode.getUserData("isLocked");
                    layerToSave.addProperty("isLocked", (Boolean) isLockedLayer);

                    for (Spatial sp : layerNode.getChildren()) {
                        JsonObject entityJSON = new JsonObject();

                        Object idObj = sp.getUserData("EntityID");
                        long idLong = (Long) idObj;
                        entityJSON.addProperty("ID", idLong);

                        // save name
                        EntityNameComponent nameComp = (EntityNameComponent) base.getEntityManager().getComponent(idLong, EntityNameComponent.class);
                        entityJSON.addProperty("IDName", nameComp.getName());

                        EntityModelPathComponent pathComp = (EntityModelPathComponent) base.getEntityManager().getComponent(idLong, EntityModelPathComponent.class);
                        entityJSON.addProperty("IDPath", pathComp.getModelPath());

                        entityJSON.addProperty("IDModel", nameComp.getName().substring(0, nameComp.getName().indexOf("_IDX")));

                        // save transforms
                        Transform trID = sp.getWorldTransform();
//                trID.getRotation().inverseLocal();
                        JsonObject transformToSave = new JsonObject();
                        transformToSave.addProperty("translationX", trID.getTranslation().getX());
                        transformToSave.addProperty("translationY", trID.getTranslation().getY());
                        transformToSave.addProperty("translationZ", trID.getTranslation().getZ());
                        transformToSave.addProperty("rotationX", trID.getRotation().getX());
                        transformToSave.addProperty("rotationY", trID.getRotation().getY());
                        transformToSave.addProperty("rotationZ", trID.getRotation().getZ());
                        transformToSave.addProperty("rotationW", trID.getRotation().getW());
                        transformToSave.addProperty("scaleX", trID.getScale().getX());
                        transformToSave.addProperty("scaleY", trID.getScale().getY());
                        transformToSave.addProperty("scaleZ", trID.getScale().getZ());
                        entityJSON.add("IDTransform", transformToSave);

                        // seve data components of entity
                        ConcurrentHashMap<String, String> entityData = base.getDataManager().getEntityData(idLong);
                        JsonObject dataComponentsToSave = new JsonObject();
                        for (String strKey : entityData.keySet()) {
                            dataComponentsToSave.addProperty(strKey, entityData.get(strKey));
                        }
                        entityJSON.add("IDDataComponents", dataComponentsToSave);

                        // PLACE OF ENTITY
                        JsonObject idScenePlace = new JsonObject();
                        idScenePlace.addProperty("Scene", sceneName);
                        idScenePlace.addProperty("LayerGroup", layersGroup.getLayersGroupName());
                        idScenePlace.addProperty("Layer", (Integer) layerNode.getUserData("LayerNumber"));

                        entityJSON.add("IDScenePlace", idScenePlace);

                        entitiesToSave.add(entityJSON);
                    }

                    allLayers.add(layerToSave); // SAVE A LAYER
                }
                layerGroupToSave.add("Layers", allLayers); // SAVE ALL LAYERS
                allLayersGroups.add(layerGroupToSave); // SAVE LAYERGROUP
            }

            // save Scene
            sceneToSave.add("LayersGroups", allLayersGroups);
            allScenes.add(sceneToSave);
        }

        // save all
        saveSWEJson.add("Scenes", allScenes); // SAVE SCENES
        saveSWEJson.add("Entities", entitiesToSave); // SAVE ENTITIES
        saveJsonFile(pathToSave + ".swe", saveSWEJson);
        System.out.println("File saved: " + pathToSave + ".swe");
    }

    protected void clearScene() {
        // clear selection
        base.getSelectionManager().clearSelectionList();
        base.getSelectionManager().calculateSelectionCenter();

        // clear all scenes, layersGoups, layers
        for (EditorSceneObject scene : scenesList.values()) {
            scene.clearScene(this);
        }
        scenesList.clear();
        activeScene = null;


        // clear history
        base.getHistoryManager().clearHistory();

        // clear data components
        base.getDataManager().clearEntityData();

        // clear entities
        Map<Long, ComponentsControl> allControls = base.getEntityManager().getAllEntities();
        for (Long ID : allControls.keySet()) {
            base.getEntityManager().removeEntity(ID);
            base.getSpatialSystem().removeSpatialControl(ID);
        }
        allControls.clear();

        // clear paths cache
        sceneNameCache = null;
        scenePathCache = null;

        // clear assets list
        clearAssets();

        //clear prevew boolean
        savePreviewJ3o = false;
    }

    public void clearAssets() {
        for (Spatial str : spatialsList.values()) {
            dsk.deleteFromCache(str.getKey());
        }
        spatialsList.clear();

        for (String str : assetsList) {
            dsk.unregisterLocator(str, FileLocator.class);
        }
        assetsList.clear();

        entitiesList.clear();

        dsk.clearCache(); // clear all loaded models        
//        dsk.clearAssetEventListeners();
    }

    public void addAsset(String path) {
        String thePath = correctPath(path);
        File fl = new File(thePath);

        // registerLocator
//        System.out.println(fl.exists());
        if (!assetsList.contains(thePath)) {
            if (fl.exists()) {
                dsk.registerLocator(thePath, FileLocator.class);
                findFiles(thePath, thePath, "j3o");
            }
            assetsList.add(thePath);
        }

    }

    protected void savePreviewScene(String pathToSave, String sceneSWEName) {
        // Saving scene with linked Nodes to j3o (for scene viewing)
        Node sceneSavePreview = new Node(sceneSWEName);

        for (EditorSceneObject sceneObj : scenesList.values()) {
            Node sceneToSave = new Node(sceneObj.getSceneName());
            Node sceneNode = sceneObj.getSceneNode();

            // save Data of a Scene
            sceneToSave.setUserData("isActive", sceneNode.getUserData("isActive"));
            sceneToSave.setUserData("isEnabled", sceneNode.getUserData("isEnabled"));
            if ((Boolean) sceneNode.getUserData("isEnabled") == false) {
                sceneToSave.setCullHint(Spatial.CullHint.Always);
            }

            sceneSavePreview.attachChild(sceneToSave);

            // get layersGroups of a Scene
            for (EditorLayersGroupObject layersGroupObj : sceneObj.getLayersGroupsList().values()) {
                Node layersGroupToSave = new Node(layersGroupObj.getLayersGroupName());
                Node layersGroupNode = layersGroupObj.getLayersGroupNode();

                // save Data of a layerGroup
                layersGroupToSave.setUserData("isActive", layersGroupNode.getUserData("isActive"));
                layersGroupToSave.setUserData("isEnabled", layersGroupNode.getUserData("isEnabled"));
                if ((Boolean) layersGroupNode.getUserData("isEnabled") == false) {
                    layersGroupToSave.setCullHint(Spatial.CullHint.Always);
                }

                sceneToSave.attachChild(layersGroupToSave);

                // save Layers
                for (Node layerNode : layersGroupObj.getLayers()) {

                    if (layerNode.getChildren().size() > 0) {
                        Node layerToSave = new Node(layerNode.getName());

                        // save Data of a Layer
                        layerToSave.setUserData("isActive", layerNode.getUserData("isActive"));
                        layerToSave.setUserData("isLocked", layerNode.getUserData("isLocked"));
                        layerToSave.setUserData("isEnabled", layerNode.getUserData("isEnabled"));
                        if ((Boolean) layerNode.getUserData("isEnabled") == false) {
                            layerToSave.setCullHint(Spatial.CullHint.Always);
                        }

                        for (Spatial spEntity : layerNode.getChildren()) {


                            // load entity
                            Object IDObj = spEntity.getUserData("EntityID");
                            Object pathComponent = base.getEntityManager().getComponent((Long) IDObj, EntityModelPathComponent.class);
                            EntityModelPathComponent modelPath = (EntityModelPathComponent) pathComponent;

                            Spatial linkedEntity;
                            if (entitiesList.containsValue(modelPath.getModelPath())) {
                                ModelKey mkLinkToScene = new ModelKey(modelPath.getModelPath());
                                linkedEntity = new AssetLinkNode(mkLinkToScene);
                            } else {
                                linkedEntity = notFoundModel.clone(false);
                            }


                            // set name
                            Object modelNameObj = base.getEntityManager().getComponent((Long) IDObj, EntityNameComponent.class);
                            EntityNameComponent modelName = (EntityNameComponent) modelNameObj;
                            linkedEntity.setName(modelName.getName());
                            linkedEntity.setLocalTransform(spEntity.getWorldTransform());

                            // set components
                            ConcurrentHashMap<String, String> dataComponents = base.getDataManager().getEntityData((Long) IDObj);
                            for (String key : dataComponents.keySet()) {
                                linkedEntity.setUserData(key, dataComponents.get(key));
                            }
                            linkedEntity.setUserData("IDPath", modelPath.getModelPath());
                            linkedEntity.setUserData("IDName", modelName.getName());

                            // add entity to a layer
                            layerToSave.attachChild(linkedEntity);
                        }

                        layersGroupToSave.attachChild(layerToSave);
                    }
                }
            }
        }




        // save node
        binaryExport(pathToSave + sceneSWEName + "_preview", sceneSavePreview);

        // clear node
        sceneSavePreview.detachAllChildren();
        sceneSavePreview = null;
    }

    private void binaryExport(String fullPath, Node saveNode) {

        File MaFile = new File(fullPath + ".j3o");
        MaFile.setWritable(true);
        MaFile.canWrite();
        MaFile.canRead();


        try {
            BinaryExporter exporter = BinaryExporter.getInstance();
            exporter.save(saveNode, MaFile);
//            BinaryExporter.getInstance().save(saveNode, MaFile);
        } catch (IOException ex) {
            System.out.println("Baddddd Saveee");

        }

    }

    private Node loadSpatial(String path) {
        // setup Entity
        Node model = null;
        if (spatialsList.contains(path) == false) {
            AssetInfo theAsset = assetMan.locateAsset(new AssetKey(path));

            if (theAsset != null) {
                Node loadedModel = (Node) dsk.loadModel(path);
                spatialsList.put(path, loadedModel);
                model = loadedModel.clone(false);
            } else {
                model = (Node) notFoundModel.clone(false);
            }


        } else {
            model = (Node) spatialsList.get(path).clone(false);
        }

        return model;
    }

    public Long createEntityModel(String name, String path, Long existedID) {

        Node model = loadSpatial(path);

        Vector3f camHelperPosition = base.getCamManager().getCamTrackHelper().getWorldTranslation();
        model.setLocalTranslation(camHelperPosition);

        long ent;
        if (existedID == null) {
            ent = base.getEntityManager().createEntity();
        } else {
            ent = existedID;
            base.getEntityManager().setComponentControl(ent, null);
        }

        base.getDataManager().setEntityData(ent, new ConcurrentHashMap<String, String>());
        ComponentsControl components = base.getEntityManager().getComponentControl(ent);

        EntityModelPathComponent modelPath = new EntityModelPathComponent(path);
        components.setComponent(modelPath);

        EntityNameComponent nameComponent = new EntityNameComponent(name + "_IDX" + ent);
        components.setComponent(nameComponent);
        model.setName(nameComponent.getName());

        EntitySpatialsControl spatialControl = base.getSpatialSystem().setSpatialControl(model, ent, base.getEntityManager().getComponentControl(ent));
        spatialControl.setType(EntitySpatialsControl.SpatialType.Node);
//        spatialControl.recurseNodeID(model);

        return ent;
    }

    public void removeClones(String path) {
        List<Long> selList = base.getSelectionManager().getSelectionList();
        List<Long> idsToRemove = new ArrayList<Long>();
        for (Long id : selList) {

            // remove objects from the scene
            EntityModelPathComponent pathComp = (EntityModelPathComponent) base.getEntityManager().getComponent(id, EntityModelPathComponent.class);
            if (pathComp.getModelPath().equals(path)) {
                idsToRemove.add(id);
            }
        }

        for (Long removeID : idsToRemove) {
            EntityNameComponent nameToRemoveReal = (EntityNameComponent) base.getEntityManager().getComponent(removeID, EntityNameComponent.class);
            base.getGuiManager().getSceneObjectsListBox().removeItem(nameToRemoveReal.getName());
            removeEntityObject(removeID);
            selList.remove(removeID);
        }
        idsToRemove.clear();
        idsToRemove = null;
        base.getSelectionManager().calculateSelectionCenter();
    }

    public void replaceModels(String path) {
        List<Long> selList = base.getSelectionManager().getSelectionList();
        for (Long id : selList) {
            EntityModelPathComponent pathComp = (EntityModelPathComponent) base.getEntityManager().getComponent(id, EntityModelPathComponent.class);
            pathComp.setModelPath(path);

            Spatial newModel = loadSpatial(path);
            EntitySpatialsControl spControl = base.getSpatialSystem().getSpatialControl(id);
            base.getSelectionManager().removeSelectionBox((Node) spControl.getGeneralNode());
            spControl.setGeneralNode(newModel);
            base.getSelectionManager().createSelectionBox((Node) newModel);
        }

        base.getSelectionManager().calculateSelectionCenter();
    }

    public long cloneEntity(long idToClone, Node layerToClone) {
//        List<Long> selectionList = base.getSelectionManager().getSelectionList();
//        List<Long> tempList = new ArrayList<Long>();
//        for (Long idOfSelected : listOfEntities) {
        // selected entity's components
        ComponentsControl compControlSelected = base.getEntityManager().getComponentControl(idToClone);
        EntityModelPathComponent modelPathSelected = (EntityModelPathComponent) compControlSelected.getComponent(EntityModelPathComponent.class);
        Node selectedModel = (Node) base.getSpatialSystem().getSpatialControl(idToClone).getGeneralNode();
//            Node layerToClone = selectedModel.getParent();
        EntityNameComponent modelNameSelected = (EntityNameComponent) compControlSelected.getComponent(EntityNameComponent.class);

        // new entity
        String selectedName = modelNameSelected.getName().substring(0, modelNameSelected.getName().indexOf("_IDX"));
        long newID = createEntityModel(selectedName, modelPathSelected.getModelPath(), null);
        Node newModel = (Node) base.getSpatialSystem().getSpatialControl(newID).getGeneralNode();
        newModel.setLocalTransform(selectedModel.getWorldTransform());

        // Clone data
        ConcurrentHashMap<String, String> dataOfSelected = base.getDataManager().getEntityData(idToClone);
        ConcurrentHashMap<String, String> dataNew = base.getDataManager().getEntityData(newID);
        for (String key : dataOfSelected.keySet()) {
            dataNew.put(key, dataOfSelected.get(key));
        }

//            tempList.add(newID);
        layerToClone.attachChild(newModel);
//        }

        return newID;
    }

    public void removeEntityObject(long id) {
        //remove item from selection
        List<Long> selList = base.getSelectionManager().getSelectionList();
        if (selList.contains(id)) {
            Node nd = (Node) base.getSpatialSystem().getSpatialControl(id).getGeneralNode();
            base.getSelectionManager().removeSelectionBox(nd);
            nd = null;
        }

        // destroy entity
        base.getEntityManager().removeEntity(id);
        base.getSpatialSystem().removeSpatialControl(id);
        base.getDataManager().removeEntityData(id);
    }

    // Correct path for Windows OS
    protected String correctPath(String path) {
        String pathCorrected = path;

        if (File.separatorChar == '\\') {
            pathCorrected = pathCorrected.replace('\\', '/');
        }
        if (!path.endsWith("/") && path.indexOf(".") < 0) {
            pathCorrected += "/";
        }

        return pathCorrected;
    }

    protected void saveJsonFile(String pathToSave, JsonObject saveJson) {
        try {
            File saveFile = new File(pathToSave);
            saveFile.setReadable(true);
            saveFile.setWritable(true);

            Gson gs = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();

            FileWriter fileToSave = new FileWriter(saveFile);
            fileToSave.write(gs.toJson(saveJson));
            fileToSave.flush();
            fileToSave.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initializeTempLighting() {

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(1.1f, 1, 0.95f, 1));
        root.addLight(dl);

        al = new AmbientLight();
        al.setColor(new ColorRGBA(1, 1, 2, 1));
        root.addLight(al);
    }

    protected JsonObject loadToJsonFile(String path, boolean isClassPath) {
        FileReader fileRead = null;
        JsonObject jsObj = null;

        try {
            InputStream inputStr = null;
            if (isClassPath) {
                inputStr = getClass().getResourceAsStream(path);
            } else {
                inputStr = new FileInputStream(path);
            }

            InputStreamReader inputStrRead = new InputStreamReader(inputStr, "UTF-8");
            BufferedReader bufRead = new BufferedReader(inputStrRead);
//            jsObj = (JSONObject) json.parse(br);
            jsObj = new JsonParser().parse(bufRead).getAsJsonObject();
//            fileRead.close();
            bufRead.close();
            bufRead = null;
            inputStrRead = null;

        } catch (IOException ex) {
            throw new UnsupportedOperationException("bad JSON file");
//            System.out.println("bad JSON file");
        }

        return jsObj;
    }

//    protected Long addEntityToScene(String name) {
//        return createEntityModel(name, entitiesList.get(name), null);
//    }
    // Recursive search of files
    protected void findFiles(String dirEntity, String dirEntityRecursive, String fileExtension) {
        System.out.println("ooooooooo LOAD entity Dir : " + dirEntity);
        File dir = new File(dirEntityRecursive);
        File[] a = dir.listFiles();

        for (File f : a) {
            if (f.isDirectory() && f.getName().indexOf("svn") < 0) {
                // Recursive search
                System.out.println("****** CHECKing Dir : " + f.getName());
                String recursDir = dirEntityRecursive + f.getName() + "/";
                findFiles(dirEntity, recursDir, fileExtension);
            } else if (f.getName().endsWith("." + fileExtension)) {

                String strF = f.getName();
                String modelName = f.getName().substring(0, f.getName().indexOf("." + fileExtension));
                String modelRelativePath = f.getAbsolutePath().substring(dirEntity.length(), f.getAbsolutePath().length());

                // Add found models
                if (!entitiesList.containsKey(modelName)) {
                    entitiesList.put(modelName, modelRelativePath);
                } else {

                    String newModelName = modelName;
                    int index = 0;
                    do {
                        newModelName = modelName + "_N" + index;
                    } while (!entitiesList.containsKey(modelName));

                    entitiesList.put(newModelName, modelRelativePath);
                }

                System.out.println("========>>FOUND ENTITY :: " + modelRelativePath);
            }
        }
    }

    public static List<String> getAssetsList() {
        return assetsList;
    }

    public static ConcurrentHashMap<String, String> getEntitiesListsList() {
        return entitiesList;
    }

    protected String getScenePath() {
        return scenePathCache;
    }

    protected void setScenePath(String scenePath) {
        scenePath = scenePath;
    }

    protected String getSceneName() {
        return sceneNameCache;
    }

    protected void setSceneName(String sceneName) {
        sceneName = sceneName;
    }

    public void newScene() {
        clearScene();
    }

    public boolean getSavePreviewJ3o() {
        return savePreviewJ3o;
    }

    public void setSavePreviewJ3o(boolean savePreviewJ3o) {
        this.savePreviewJ3o = savePreviewJ3o;
        System.out.println(savePreviewJ3o);
    }

    public static ConcurrentHashMap<String, EditorSceneObject> getScenesList() {
        return scenesList;
    }

    public EditorSceneObject createSceneObject(String sceneName) {
        EditorSceneObject newScene = new EditorSceneObject(root, sceneName);
        newScene.setSceneEnabled(true);
//        setActiveSceneObject(newScene);
        scenesList.put(sceneName, newScene);

        return newScene;
    }

    public EditorSceneObject getActiveSceneObject() {
        return activeScene;
    }

    public void setActiveSceneObject(EditorSceneObject activeScene) {
        if (this.activeScene != null) {
            this.activeScene.getSceneNode().setUserData("isActive", false); // old active
        }

        if (activeScene != null) {
            this.activeScene = activeScene;
            this.activeScene.getSceneNode().setUserData("isActive", true);  // new active            
        } else {
            this.activeScene = null;
        }

    }

    public EditorSceneObject cloneSceneObject(String newName, EditorSceneObject sceneToClone) {
        // CLONE SCENE
        EditorSceneObject newActiveClonedScene = createSceneObject(newName);
        newActiveClonedScene.setSceneEnabled((Boolean) sceneToClone.getSceneNode().getUserData("isEnabled"));

        // parse LayersGroups
        for (EditorLayersGroupObject layersGroupToClone : sceneToClone.getLayersGroupsList().values()) {
            newActiveClonedScene.cloneLayersGroup(layersGroupToClone.getLayersGroupName(), layersGroupToClone, this);
        }

        // set the same active layer and layersgroup
        newActiveClonedScene.setActivelayersGroup(newActiveClonedScene.getLayersGroupsList().get(sceneToClone.getActivelayersGroup().getLayersGroupName()));
        int oldActiveLayer = (Integer) sceneToClone.getActivelayersGroup().getActiveLayer().getUserData("LayerNumber");
        newActiveClonedScene.getActivelayersGroup().setActiveLayer(newActiveClonedScene.getActivelayersGroup().getLayer(oldActiveLayer));


        return newActiveClonedScene;
    }
}
