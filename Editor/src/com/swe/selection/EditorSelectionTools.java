/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.selection;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.swe.EditorBaseManager;

/**
 *
 * @author mifth
 */
public class EditorSelectionTools {

    private AssetManager assetMan;
    private Node root, guiNode;
    private Application app;
    private EditorBaseManager base;
    private EditorSelectionManager selManager;
//    private Node selectable;
    private Vector2f rectanglePointA, rectanglePointB, rectanglePosition;
    private Node rectangleSelection = new Node("rectangleSelection");

    public EditorSelectionTools(Application app, EditorBaseManager base, EditorSelectionManager selManager) {
        this.app = app;
        this.base = base;
        this.selManager = selManager;
        assetMan = this.app.getAssetManager();
        root = (Node) this.app.getViewPort().getScenes().get(0);
        guiNode = (Node) this.app.getGuiViewPort().getScenes().get(0);
//        selectable = (Node) root.getChild("selectableNode");

        createRectangle();
    }

    private void createRectangle() {

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.9f, 0.4f, 0.6f, 1));
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        Line ln1 = new Line(new Vector3f(-0.5f, 0.5f, 0), new Vector3f(0.5f, 0.5f, 0));
        ln1.setLineWidth(1);
        Geometry geoLn1 = new Geometry("ln1", ln1);
        geoLn1.setMaterial(mat);
        rectangleSelection.attachChild(geoLn1);

        Line ln2 = new Line(new Vector3f(-0.5f, -0.5f, 0), new Vector3f(0.5f, -0.5f, 0));
        ln2.setLineWidth(1);
        Geometry geoLn2 = new Geometry("ln2", ln2);
        geoLn2.setMaterial(mat);
        rectangleSelection.attachChild(geoLn2);

        Line ln3 = new Line(new Vector3f(-0.5f, -0.5f, 0), new Vector3f(-0.5f, 0.5f, 0));
        ln3.setLineWidth(1);
        Geometry geoLn3 = new Geometry("ln3", ln3);
        geoLn3.setMaterial(mat);
        rectangleSelection.attachChild(geoLn3);

        Line ln4 = new Line(new Vector3f(0.5f, -0.5f, 0), new Vector3f(0.5f, 0.5f, 0));
        ln4.setLineWidth(1);
        Geometry geoLn4 = new Geometry("ln4", ln4);
        geoLn4.setMaterial(mat);
        rectangleSelection.attachChild(geoLn4);

        guiNode.attachChild(rectangleSelection);
    }

    protected void selectMouseClick() {
        CollisionResult colResult = null;
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray();
        Vector3f pos = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0f).clone();
        Vector3f dir = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 1f).clone();
        dir.subtractLocal(pos).normalizeLocal();
        ray.setOrigin(pos);
        ray.setDirection(dir);

        boolean isSceneEnabled = (Boolean) base.getSceneManager().getActiveSceneObject().getSceneNode().getUserData("isEnabled");
        boolean isLayersGoupEnabled = (Boolean) base.getSceneManager().getActiveSceneObject().getActivelayersGroup().getLayersGroupNode().getUserData("isEnabled");

        // if scene and layersGroup are enabled
        if (isLayersGoupEnabled && isSceneEnabled) {
            
            // Collide with activeLayersGroup
            base.getSceneManager().getActiveSceneObject().getActivelayersGroup().getLayersGroupNode().collideWith(ray, results);

            if (results.size() > 0) {

                for (CollisionResult colRes : results) {
                    if (!colRes.getGeometry().getName().equals("SelectionTempMesh")) {
                        // get geometry and id
                        Geometry geoCollided = colRes.getGeometry();
                        Object idObj = colRes.getGeometry().getUserData("EntityID");
                        long id = (Long) idObj;

                        // check layer's locking
                        Node entityNode = (Node) base.getSpatialSystem().getSpatialControl(id).getGeneralNode();
                        Object isLayerLockedObj = entityNode.getParent().getUserData("isLocked");
                        boolean isLayerLocked = (Boolean) isLayerLockedObj;

                        if (!isLayerLocked) {
                            colResult = colRes;
                            idObj = null;
                            geoCollided = null;
                            entityNode = null;
                            isLayerLockedObj = null;
                            break;
                        }

                        idObj = null;
                        geoCollided = null;
                        entityNode = null;
                        isLayerLockedObj = null;
                    }
                }

                //select entity
                if (colResult != null) {
                    Object idObj = colResult.getGeometry().getUserData("EntityID");
                    long id = (Long) idObj;
                    selManager.selectEntity(id, selManager.getSelectionMode());
                    selManager.calculateSelectionCenter();
                } else {
                    if (selManager.getSelectionMode() == EditorSelectionManager.SelectionMode.Normal) {
                        base.getSelectionManager().clearSelectionList();
                    }
                }

            } else {
                if (selManager.getSelectionMode() == EditorSelectionManager.SelectionMode.Normal) {
                    base.getSelectionManager().clearSelectionList();
                }
            }
        }
    }

    protected void drawRectangle() {

        // set first point of rectangle selection
        if (rectanglePointA == null) {
            rectanglePointA = app.getInputManager().getCursorPosition().clone();
        }

        // set second point of rectangle selection
        rectanglePointB = app.getInputManager().getCursorPosition();

        // calculate position
        rectanglePosition = rectanglePointA.add(rectanglePointB.subtract(rectanglePointA).multLocal(0.5f));

        float pointX1 = Math.min(rectanglePointA.getX(), rectanglePointB.getX());
        float pointX2 = Math.max(rectanglePointA.getX(), rectanglePointB.getX());
        float pointY1 = Math.min(rectanglePointA.getY(), rectanglePointB.getY());
        float pointY2 = Math.max(rectanglePointA.getY(), rectanglePointB.getY());

        // set position
        rectangleSelection.setLocalTranslation(new Vector3f(rectanglePosition.getX(), rectanglePosition.getY(), 1));


        // set scale
        rectangleSelection.setLocalScale(new Vector3f((pointX2 - pointX1), (pointY2 - pointY1), 1));

        guiNode.attachChild(rectangleSelection);
    }

    protected Node getRectangleSelection() {
        return rectangleSelection;
    }

    protected void clearRectangle() {
        rectanglePointA = null;
        guiNode.detachChild(rectangleSelection);
    }
}
