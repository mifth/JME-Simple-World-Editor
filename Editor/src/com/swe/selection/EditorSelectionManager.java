/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.selection;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.debug.WireBox;
import com.swe.EditorBaseManager;
import com.swe.scene.EditorLayersGroupObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mifth
 */
public class EditorSelectionManager extends AbstractAppState {

    private AssetManager assetMan;
    private Node root, guiNode;
    private Application app;
    private EditorBaseManager base;
    private static List<Long> selectionList;
    private Transform selectionCenter;
    private SelectionToolType selectionToolType;
    private EditorSelectionTools selectionTools;
    private boolean isActive, doActiveRectange;
    private SelectionMode selectionMode;
    private long lastSelected;

    public enum SelectionToolType {

        All, MouseClick, Rectangle, Polygon
    };

    public enum SelectionMode {

        Normal, Additive, Substractive
    };

    public EditorSelectionManager(Application app, EditorBaseManager base) {

        this.app = app;
        this.base = base;
        assetMan = this.app.getAssetManager();
        root = (Node) this.app.getViewPort().getScenes().get(0);
        guiNode = (Node) this.app.getGuiViewPort().getScenes().get(0);

        isActive = false;
        doActiveRectange = false;
        selectionCenter = null;
        selectionList = new ArrayList<Long>();

        selectionTools = new EditorSelectionTools(this.app, this.base, this);
        selectionToolType = SelectionToolType.MouseClick;
        selectionMode = selectionMode.Normal;


    }

    public boolean activate() {
        boolean result = false;

        if (selectionToolType == SelectionToolType.MouseClick) {
            selectionTools.selectMouseClick();
//            base.getGuiManager().setSelectedObjectsList();
            result = true;
        } else if (selectionToolType == SelectionToolType.Rectangle) {
//            selectionTools.drawRectangle();
            isActive = true;
            doActiveRectange = true;
            result = true;
        }

        return result;

    }

    public void deactivate() {

        // SELECT ENTITIES OF THE RECTANGLE TOOL
        if (doActiveRectange && isActive) {
            boolean isSceneEnabled = (Boolean) base.getSceneManager().getActiveSceneObject().getSceneNode().getUserData("isEnabled");
            boolean isLayersGoupEnabled = (Boolean) base.getSceneManager().getActiveSceneObject().getActivelayersGroup().getLayersGroupNode().getUserData("isEnabled");

            // if scene and layersGroup are enabled
            if (isLayersGoupEnabled && isSceneEnabled) {
                rectangleSelectEntities();
            }
            selectionTools.clearRectangle();
            doActiveRectange = false;
            
            System.out.println("Deactivate Rectangle");
        }

        isActive = false;
        calculateSelectionCenter();

        // SET HISTORY
        System.out.println("selHistory");
        base.getHistoryManager().setNewSelectionHistory(selectionList);


    }

    public void selectEntity(long ID, SelectionMode mode) {
        Node entityToSelect = (Node) base.getSpatialSystem().getSpatialControl(ID).getGeneralNode();
        Object isLayerLockedObj = entityToSelect.getParent().getUserData("isLocked");
        boolean isLayerLocked = (Boolean) isLayerLockedObj;

        if (!isLayerLocked) {
            if (mode == SelectionMode.Normal) {
                // remove selection boxes
                for (Long idToRemove : selectionList) {
                    removeSelectionBox((Node) base.getSpatialSystem().getSpatialControl(idToRemove).getGeneralNode());
                }
                selectionList.clear();

                // add to selection
                selectionList.add(ID);
                createSelectionBox(entityToSelect);

            } else if (mode == SelectionMode.Additive) {
                if (selectionList.contains(ID)) {
                    selectionList.remove(ID);
                    removeSelectionBox(entityToSelect); // remove selection mesh
                } else {
                    selectionList.add(ID);
                    createSelectionBox(entityToSelect);
                }
            }
        }
    }

    public void rectangleSelectEntities() {
        Vector2f centerCam = new Vector2f(app.getCamera().getWidth() * 0.5f, app.getCamera().getHeight() * 0.5f);
        Node rectangle = selectionTools.getRectangleSelection();
        Vector3f rectanglePosition = rectangle.getLocalTranslation();

        // Remove old selection
        if (selectionMode == SelectionMode.Normal) {
            clearSelectionList();
        }


        EditorLayersGroupObject activelayersGroup = base.getSceneManager().getActiveSceneObject().getActivelayersGroup();
        boolean isSceneEnabled = (Boolean) base.getSceneManager().getActiveSceneObject().getSceneNode().getUserData("isEnabled");
        boolean isLayersGoupEnabled = (Boolean) base.getSceneManager().getActiveSceneObject().getActivelayersGroup().getLayersGroupNode().getUserData("isEnabled");

        // if scene and layersGroup are enabled
        if (isLayersGoupEnabled && isSceneEnabled) {
            List<Node> lst = activelayersGroup.getLayers();

            for (Node layer : lst) {
                // check if layer is enabled
                Object boolObj = layer.getUserData("isEnabled");
                boolean boolEnabled = (Boolean) boolObj;
                Object boolLockedObj = layer.getUserData("isLocked");
                boolean boolLocked = (Boolean) boolLockedObj;

                if (boolEnabled && !boolLocked) {
                    for (Spatial sp : layer.getChildren()) {

                        Vector3f spScreenPos = app.getCamera().getScreenCoordinates(sp.getWorldTranslation());
                        float spScreenDistance = centerCam.distance(new Vector2f(spScreenPos.getX(), spScreenPos.getY()));

                        if (spScreenPos.getZ() < 1f) {

                            float pointMinX = Math.min(rectanglePosition.getX(), spScreenPos.getX());
                            float pointMaxX = Math.max(rectanglePosition.getX(), spScreenPos.getX());
                            float pointMinY = Math.min(rectanglePosition.getY(), spScreenPos.getY());
                            float pointMaxY = Math.max(rectanglePosition.getY(), spScreenPos.getY());

                            float distX = pointMaxX - pointMinX;
                            float distY = pointMaxY - pointMinY;

                            //add to selection the spatial which is in the rectangle area
                            if (distX <= rectangle.getLocalScale().getX() * 0.5f
                                    && distY <= rectangle.getLocalScale().getY() * 0.5f) {
                                Object spIdObj = sp.getUserData("EntityID");
                                long spId = (Long) spIdObj;
                                if (selectionMode == SelectionMode.Additive) {
                                    selectEntity(spId, selectionMode);
                                } else if (selectionMode == SelectionMode.Normal) {
                                    selectionList.add(spId);
                                    Node nodeToSelect = (Node) base.getSpatialSystem().getSpatialControl(spId).getGeneralNode();
                                    createSelectionBox(nodeToSelect);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public void createSelectionBox(Node nodeSelect) {
        Material mat_box = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("Color", new ColorRGBA(0.5f, 0.3f, 0.1f, 1));
        WireBox wbx = new WireBox();
//        BoundingBox = new BoundingBox();
        Transform tempScale = nodeSelect.getLocalTransform().clone();
        nodeSelect.setLocalTransform(new Transform());
        wbx.fromBoundingBox((BoundingBox) nodeSelect.getWorldBound());

        Geometry bx = new Geometry("SelectionTempMesh", wbx);
        bx.setMaterial(mat_box);
        bx.setLocalTranslation(nodeSelect.getWorldBound().getCenter());
        nodeSelect.attachChild(bx);
        
        nodeSelect.setLocalTransform(tempScale);

    }

    public void removeSelectionBox(Node nodeSelect) {
        nodeSelect.detachChild(nodeSelect.getChild("SelectionTempMesh"));
    }

    public void clearSelectionList() {
        for (Long id : selectionList) {
            removeSelectionBox((Node) base.getSpatialSystem().getSpatialControl(id).getGeneralNode());
        }
        selectionList.clear();
    }

    public Transform getSelectionCenter() {
        return selectionCenter;
    }

    public void setSelectionCenter(Transform selectionTransform) {
        this.selectionCenter = selectionTransform;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void calculateSelectionCenter() {
        if (selectionList.size() == 0) {
            selectionCenter = null;
        } else if (selectionList.size() == 1) {
            Spatial nd = base.getSpatialSystem().getSpatialControl(selectionList.get(0)).getGeneralNode();
            selectionCenter = nd.getLocalTransform().clone();
        } else if (selectionList.size() > 1) {

            if (selectionCenter == null) {
                selectionCenter = new Transform();
            }

            // FIND CENTROID OF center POSITION
            Vector3f centerPosition = new Vector3f();
            for (Long ID : selectionList) {
//                // POSITION
                Spatial ndPos = base.getSpatialSystem().getSpatialControl(ID).getGeneralNode();
                centerPosition.addLocal(ndPos.getWorldTranslation());
            }
            centerPosition.divideLocal(selectionList.size());
            selectionCenter.setTranslation(centerPosition);

            // Rotation of the last selected is Local Rotation (like in Blender)
            Quaternion rot = base.getSpatialSystem().getSpatialControl(selectionList.get(selectionList.size() - 1)).getGeneralNode().getLocalRotation();
//                TransformComponent trLastSelected = (TransformComponent) base.getEntityManager().getComponent(selectionList.get(selectionList.size() - 1), TransformComponent.class);
            selectionCenter.setRotation(rot); //Local coordinates of the last object            
        }

        //set the last selected color
        if (selectionList.size() > 0) {
            // set for previous selected
            if (selectionList.size() > 1 && selectionList.contains(lastSelected)) {
                Node ndPrevious = (Node) base.getSpatialSystem().getSpatialControl(lastSelected).getGeneralNode();
                Geometry geoBoxPrevious = (Geometry) ndPrevious.getChild("SelectionTempMesh");
                geoBoxPrevious.getMaterial().setColor("Color", new ColorRGBA(0.5f, 0.3f, 0.1f, 1));
            }

            // set for new selected
            long lastID = selectionList.get(selectionList.size() - 1);
            Node ndLast = (Node) base.getSpatialSystem().getSpatialControl(lastID).getGeneralNode();
            Geometry geoBoxLast = (Geometry) ndLast.getChild("SelectionTempMesh");
            geoBoxLast.getMaterial().setColor("Color", new ColorRGBA(0.8f, 0.6f, 0.2f, 1));
            lastSelected = lastID;
        }

    }

    public List<Long> getSelectionList() {
        return selectionList;
    }

    public SelectionToolType getSelectionTool() {
        return selectionToolType;
    }

    public void setSelectionTool(SelectionToolType selectionTool) {
        this.selectionToolType = selectionTool;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    @Override
    public void update(float tpf) {

        if (isActive && doActiveRectange) {
            selectionTools.drawRectangle();
        }
    }

}
