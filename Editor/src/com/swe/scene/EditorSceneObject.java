package com.swe.scene;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.swe.es.EntityManager;
import com.swe.es.EntitySpatialsSystem;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mifth
 */
public class EditorSceneObject {

    private Node rootNode, allGroupsNode;
    private String sceneName;
    private ConcurrentHashMap<String, EditorLayersGroupObject> layersGroupsList;
    private Node sceneNode;
    private EditorLayersGroupObject activelayerGroup;

    public EditorSceneObject(Node rootNode, String sceneName) {
        this.rootNode = rootNode;
        this.sceneName = sceneName;

        layersGroupsList = new ConcurrentHashMap<String, EditorLayersGroupObject>();

        createSceneNode(sceneName);
    }

    private void createSceneNode(String sceneName) {
        //scene
        sceneNode = new Node(sceneName);
        sceneNode.setUserData("isEnabled", true);
        sceneNode.setUserData("isActive", false);

        // general Node of all layerGroups
        allGroupsNode = new Node("LayerGroups");
        sceneNode.attachChild(allGroupsNode);

    }

    public void setSceneEnabled(boolean enable) {
        sceneNode.setUserData("isEnabled", enable);

        if (enable) {
            if (!rootNode.hasChild(sceneNode)) {
                rootNode.attachChild(sceneNode);
            }
        } else {
            if (rootNode.hasChild(sceneNode)) {
                rootNode.detachChild(sceneNode);
            }
        }
    }

    public EditorLayersGroupObject createLayersGroup(String layersGroupName) {
        // First Layer Group
        EditorLayersGroupObject layersGroup = new EditorLayersGroupObject(allGroupsNode, layersGroupName, sceneName);
        layersGroupsList.put(layersGroupName, layersGroup);
        
        return layersGroup;
    }

    public void removeLayersGroup(String layersGroupName, EditorSceneManager sceneManager) {
        layersGroupsList.get(layersGroupName).clearLayersGroup(sceneManager);
        layersGroupsList.remove(layersGroupName);
    }

    public Node getSceneNode() {
        return sceneNode;
    }

    public EditorLayersGroupObject getActivelayersGroup() {
        return activelayerGroup;
    }

    public void setActivelayersGroup(EditorLayersGroupObject activelayerGroup) {

        if (this.activelayerGroup != null) {
            this.activelayerGroup.getLayersGroupNode().setUserData("isActive", false); // old active
        }

        if (activelayerGroup != null) {
            this.activelayerGroup = activelayerGroup;
            this.activelayerGroup.getLayersGroupNode().setUserData("isActive", true);  // new active
        } else {
            this.activelayerGroup = null;
        }

    }

    public Node getAllGroupsNode() {
        return allGroupsNode;
    }

    public ConcurrentHashMap<String, EditorLayersGroupObject> getLayersGroupsList() {
        return layersGroupsList;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
        sceneNode.setName(this.sceneName);
    }

    public void renameScene(String newName) {
        setSceneName(newName);
        for (EditorLayersGroupObject layersGroupToChange : layersGroupsList.values()) {
            layersGroupToChange.getLayersGroupNode().setUserData("SceneName", newName);

            for (Node layerToChange : activelayerGroup.getLayers()) {
                layerToChange.setUserData("SceneName", newName);
            }
        }
    }

    public void clearScene(EditorSceneManager sceneManager) {
        for (EditorLayersGroupObject layersGroup : layersGroupsList.values()) {
            layersGroup.clearLayersGroup(sceneManager);
        }
        layersGroupsList.clear();
        sceneNode.detachAllChildren();
        activelayerGroup = null;
    }

    public EditorLayersGroupObject cloneLayersGroup(String newLayersGroupName, EditorLayersGroupObject layersGroupToClone, EditorSceneManager sceneManager) {
            
            EditorLayersGroupObject newActiveLayersGroup = createLayersGroup(newLayersGroupName);

            newActiveLayersGroup.setLayersGroupEnabled((Boolean) layersGroupToClone.getLayersGroupNode().getUserData("isEnabled"));

            // parse Layers
            for (Node layerNodeToClone : layersGroupToClone.getLayers()) {
                int layerNumb = (Integer) layerNodeToClone.getUserData("LayerNumber");

                Node newLayer = newActiveLayersGroup.getLayer(layerNumb);
                newLayer.setUserData("isLocked", (Boolean) layerNodeToClone.getUserData("isLocked"));
                boolean isClonedLayerEnabled = (Boolean) layerNodeToClone.getUserData("isEnabled");
                if (isClonedLayerEnabled) {
                    newActiveLayersGroup.enableLayer(layerNumb);
                }


                // parse the layer's children
                for (Spatial childSp : layerNodeToClone.getChildren()) {

                    long idToClone = (Long) childSp.getUserData("EntityID");
                    long clonedID = sceneManager.cloneEntity(idToClone, newLayer);
                }
            }
        
        return newActiveLayersGroup;
        
    }
    

}