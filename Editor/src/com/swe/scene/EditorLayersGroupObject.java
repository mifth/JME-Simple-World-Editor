/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.scene;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.swe.EditorBaseManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mifth
 */
public class EditorLayersGroupObject {

    private Node allGroupsNode;
    private Application app;
    private List<Node> layersList;
    private Node activeLayer;
    private Node layersGroupNode;
    private String layersGroupName;

    public EditorLayersGroupObject(Node allGroupsNode, String layersGroupName, String sceneName) {
        this.allGroupsNode = allGroupsNode;
        this.layersGroupName = layersGroupName;

        layersList = new ArrayList<Node>();
        layersGroupNode = new Node(this.layersGroupName); // new layerGroup node
        createLayers(sceneName);
//        enableLayer(1);

        layersGroupNode.setUserData("isEnabled", true);
        layersGroupNode.setUserData("isActive", false);
        layersGroupNode.setUserData("SceneName", sceneName);
        allGroupsNode.attachChild(layersGroupNode);
    }

    private void createLayers(String sceneName) {
        for (int i = 0; i < 40; i++) {
            Node layerNode = new Node("layer" + (i + 1));
            layerNode.setUserData("LayerNumber", i + 1);
            layerNode.setUserData("LayersGroupName", layersGroupName);
            layerNode.setUserData("SceneName", sceneName);
            layerNode.setUserData("isEnabled", false);
            layerNode.setUserData("isLocked", false);
            layerNode.setUserData("isActive", false);
            layersList.add(layerNode);
        }
    }

    public void setLayersGroupEnabled(boolean enable) {
        layersGroupNode.setUserData("isEnabled", enable);

        if (enable) {
            if (!allGroupsNode.hasChild(layersGroupNode)) {
                allGroupsNode.attachChild(layersGroupNode);
            }
        } else {
            if (allGroupsNode.hasChild(layersGroupNode)) {
                allGroupsNode.detachChild(layersGroupNode);
            }
        }
    }

    public void enableLayer(int layerNumber) {
        layersGroupNode.attachChild(getLayer(layerNumber));

        boolean isEnabled = (Boolean) getLayer(layerNumber).getUserData("isEnabled");
        if (!isEnabled) {

            if (activeLayer != null) {
                activeLayer.setUserData("isActive", false); // previous active layer
            }

            getLayer(layerNumber).setUserData("isEnabled", true);
            getLayer(layerNumber).setUserData("isActive", true);
            activeLayer = getLayer(layerNumber); // new active layer
        }

    }

    public Node getLayer(int layerNumber) {
        Node nd = layersList.get(layerNumber - 1);  // compensate the list number
        return nd;
    }

    public List<Node> getLayers() {
        return layersList;
    }

    public void addToLayer(Spatial sp, int layerNumber) {
        getLayer(layerNumber).attachChild(sp);
    }

    public Node getActiveLayer() {
        return activeLayer;
    }

    public void setActiveLayer(Node activeLayer) {

        if (this.activeLayer != null) {
            this.activeLayer.setUserData("isActive", false); // old active
        }

        if (activeLayer != null) {
            this.activeLayer = activeLayer;
            this.activeLayer.setUserData("isActive", true);  // new active            
        } else {
            this.activeLayer = null;
        }

    }

    public void clearLayersGroup(EditorSceneManager sceneManager) {
        for (Node layer : layersList) {
            
            for (Spatial sp : layer.getChildren()) {
                long id = (Long) sp.getUserData("EntityID");
                sceneManager.removeEntityObject(id);
            }
            
            layer.detachAllChildren(); // just for a case if something is left
            
            layer.setUserData("isEnabled", null);
            layer.setUserData("isLocked", null);
            layer.setUserData("isActive", null);
        }
        layersGroupNode.detachAllChildren();
        activeLayer = null;
    }

    public Node getLayersGroupNode() {
        return layersGroupNode;
    }

    public String getLayersGroupName() {
        return layersGroupName;
    }
    
    public void renameLayersGroup(String newLayersGroupName) {
        layersGroupName = newLayersGroupName;
            layersGroupNode.setUserData("LayersGroupName", newLayersGroupName);

            for (Node layerToChange : layersList) {
                layerToChange.setUserData("LayersGroupName", newLayersGroupName);
            }
    }
}