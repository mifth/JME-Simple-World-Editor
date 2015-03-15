/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.transform;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.swe.EditorBaseManager;
import java.util.List;

/**
 *
 * @author mifth
 */
public class EditorTransformManager extends AbstractAppState {

    private Node transformTool;
    private Node moveTool, rotateTool, scaleTool, collisionPlane;
    private Transform selectionTransformCenter, actionCenter;
    private Node root, guiNode;
    private TransformToolType transformType;
    private PickedAxis pickedAxis;
    private AssetManager assetMan;
    private Application app;
    private boolean isActive;
//    private Geometry testGeo;
    private Vector3f deltaMoveVector;
    private EditorBaseManager base;
    private Node tranformParentNode;
    // tools
    private EditorTransformMoveTool moveToolObj;
    private EditorTransformRotateTool rotateToolObj;
    private EditorTransformScaleTool scaleToolObj;
    private Node ndParent1;
    private Node ndParent2;
    private TransformCoordinates trCoordinates;
    private EditorTransformConstraint constraintTool;

    public enum TransformToolType {

        MoveTool, RotateTool, ScaleTool, None
    };

    public enum TransformCoordinates {

        WorldCoords, LocalCoords, ViewCoords
    };

    protected enum PickedAxis {

        X, Y, Z, XY, XZ, YZ, View, scaleAll, None
    };

    public EditorTransformManager(Application app, EditorBaseManager base) {

        this.app = app;
        this.base = base;
        assetMan = app.getAssetManager();
        root = (Node) app.getViewPort().getScenes().get(0);
        guiNode = (Node) app.getGuiViewPort().getScenes().get(0);

        isActive = false;
        ndParent1 = new Node();
        ndParent2 = new Node();

        transformTool = new Node("transformTool");
        root.attachChild(transformTool);

        createManipulators();
        tranformParentNode = new Node("tranformParentNode");
//        Node selectableNode = (Node) root.getChild("selectableNode");
        root.attachChild(tranformParentNode);

        pickedAxis = PickedAxis.None;
        transformType = TransformToolType.MoveTool;  //default type
        trCoordinates = TransformCoordinates.WorldCoords;

        createCollisionPlane();
        ndParent1.attachChild(ndParent2); // this is for rotation compensation

        constraintTool = new EditorTransformConstraint();
        moveToolObj = new EditorTransformMoveTool(this, this.app, this.base, constraintTool);
        rotateToolObj = new EditorTransformRotateTool(this, this.app, this.base, constraintTool);
        scaleToolObj = new EditorTransformScaleTool(this, this.app, this.base, constraintTool);

        setTransformToolScale(0.2f);

    }

    private void createCollisionPlane() {
        float size = 20000;
        Geometry g = new Geometry("plane", new Quad(size, size));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setWireframe(true);
        g.setMaterial(mat);
        g.setLocalTranslation(-size / 2, -size / 2, 0);
        collisionPlane = new Node();
        collisionPlane.attachChild(g);
//        root.attachChild(collisionPlane);
    }

    public TransformToolType getTransformToolType() {
        return transformType;
    }

    public void setTransformType(TransformToolType transformType) {
        this.transformType = transformType;
    }

    protected PickedAxis getpickedAxis() {
        return pickedAxis;
    }

    protected void setPickedAxis(PickedAxis axis) {
        pickedAxis = axis;
    }

    protected Node tranformParentNode() {
        return tranformParentNode;
    }

    protected Node getCollisionPlane() {
        return collisionPlane;
    }

    protected Vector3f getDeltaMoveVector() {
        return deltaMoveVector;
    }

    protected void setDeltaMoveVector(Vector3f vec) {
        deltaMoveVector = vec;
    }

    protected Node getTranformParentNode() {
        return tranformParentNode;
    }

    public TransformCoordinates getTrCoordinates() {
        return trCoordinates;
    }

    public void setTrCoordinates(TransformCoordinates trCoordinates) {
        this.trCoordinates = trCoordinates;
    }

    protected Transform getselectionTransformCenter() {
        return selectionTransformCenter;
    }

    public boolean isIsActive() {
        return isActive;
    }

    protected Vector3f getTransformToolScale() {
        return transformTool.getLocalScale();
    }

    protected void setTransformToolScale(float newScale) {
        transformTool.setLocalScale(new Vector3f(newScale, newScale, newScale));
    }

    public EditorTransformConstraint getConstraintTool() {
        return constraintTool;
    }    
    
    protected void updateTransformWidget(Transform center) {
        if (center != null) {
            Vector3f vec = center.getTranslation().subtract(app.getCamera().getLocation()).normalize().multLocal(app.getCamera().getFrustumNear() + 0.1f);
            transformTool.setLocalTranslation(app.getCamera().getLocation().add(vec));
            transformTool.setLocalRotation(center.getRotation());
        }
    }

    protected void updateTransformCoords() {
        if (trCoordinates == TransformCoordinates.LocalCoords) {
            selectionTransformCenter = base.getSelectionManager().getSelectionCenter();
        } else if (trCoordinates == TransformCoordinates.WorldCoords) {
            selectionTransformCenter = base.getSelectionManager().getSelectionCenter().clone();
            selectionTransformCenter.setRotation(new Quaternion());
        } else if (trCoordinates == TransformCoordinates.ViewCoords) {
            selectionTransformCenter = base.getSelectionManager().getSelectionCenter().clone();
//                Quaternion viewRot = new Quaternion();
//                viewRot.lookAt(app.getCamera().getLocation(), Vector3f.UNIT_Y);
            selectionTransformCenter.setRotation(app.getCamera().getRotation().mult(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y)));
        }
    }

    private void createManipulators() {

        Material mat_red = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_red.setColor("Color", ColorRGBA.Red);

        Material mat_blue = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_blue.setColor("Color", ColorRGBA.Blue);

        Material mat_green = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_green.setColor("Color", ColorRGBA.Green);

        Material mat_white = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_white.setColor("Color", ColorRGBA.White);

        moveTool = (Node) assetMan.loadModel("Models/simpleEditor/manipulators/manipulators_move.j3o");

        moveTool.getChild("move_x").setMaterial(mat_red);
        moveTool.getChild("collision_move_x").setMaterial(mat_red);
        moveTool.getChild("collision_move_x").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_y").setMaterial(mat_blue);
        moveTool.getChild("collision_move_y").setMaterial(mat_blue);
        moveTool.getChild("collision_move_y").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_z").setMaterial(mat_green);
        moveTool.getChild("collision_move_z").setMaterial(mat_green);
        moveTool.getChild("collision_move_z").setCullHint(Spatial.CullHint.Always);
//        moveTool.getChild("move_view").setMaterial(mat_white);
//        moveTool.getChild("collision_move_view").setMaterial(mat_white);
//        moveTool.getChild("collision_move_view").setCullHint(Spatial.CullHint.Always);
        moveTool.scale(0.1f);

        rotateTool = (Node) assetMan.loadModel("Models/simpleEditor/manipulators/manipulators_rotate.j3o");
        rotateTool.getChild("rot_x").setMaterial(mat_red);
        rotateTool.getChild("collision_rot_x").setMaterial(mat_red);
        rotateTool.getChild("collision_rot_x").setCullHint(Spatial.CullHint.Always);
        rotateTool.getChild("rot_y").setMaterial(mat_blue);
        rotateTool.getChild("collision_rot_y").setMaterial(mat_blue);
        rotateTool.getChild("collision_rot_y").setCullHint(Spatial.CullHint.Always);
        rotateTool.getChild("rot_z").setMaterial(mat_green);
        rotateTool.getChild("collision_rot_z").setMaterial(mat_green);
        rotateTool.getChild("collision_rot_z").setCullHint(Spatial.CullHint.Always);
//        rotateTool.getChild("rot_view").setMaterial(mat_white);
//        rotateTool.getChild("collision_rot_view").setMaterial(mat_white);
//        rotateTool.getChild("collision_rot_view").setCullHint(Spatial.CullHint.Always);
        rotateTool.scale(0.1f);

        scaleTool = (Node) assetMan.loadModel("Models/simpleEditor/manipulators/manipulators_scale.j3o");
        scaleTool.getChild("scale_x").setMaterial(mat_red);
        scaleTool.getChild("collision_scale_x").setMaterial(mat_red);
        scaleTool.getChild("collision_scale_x").setCullHint(Spatial.CullHint.Always);
        scaleTool.getChild("scale_y").setMaterial(mat_blue);
        scaleTool.getChild("collision_scale_y").setMaterial(mat_blue);
        scaleTool.getChild("collision_scale_y").setCullHint(Spatial.CullHint.Always);
        scaleTool.getChild("scale_z").setMaterial(mat_green);
        scaleTool.getChild("collision_scale_z").setMaterial(mat_green);
        scaleTool.getChild("collision_scale_z").setCullHint(Spatial.CullHint.Always);
//        scaleTool.getChild("scale_view").setMaterial(mat_white);
//        scaleTool.getChild("collision_scale_view").setMaterial(mat_white);
//        scaleTool.getChild("collision_scale_view").setCullHint(Spatial.CullHint.Always);
        scaleTool.scale(0.1f);


    }

    public boolean activate() {

        boolean result = false;

//        if (transformType != TransformToolType.None) {

        CollisionResult colResult = null;
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray();
        Vector3f pos = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0f).clone();
        Vector3f dir = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 1f).clone();
        dir.subtractLocal(pos).normalizeLocal();
        ray.setOrigin(pos);
        ray.setDirection(dir);
        transformTool.collideWith(ray, results);

        if (results.size() > 0) {
            colResult = results.getClosestCollision();

            if (transformType == TransformToolType.MoveTool) {
                moveToolObj.setCollisionPlane(colResult);
            } else if (transformType == TransformToolType.RotateTool) {
                rotateToolObj.setCollisionPlane(colResult);
            } else if (transformType == TransformToolType.ScaleTool) {
                scaleToolObj.setCollisionPlane(colResult);
            }

//                base.getSelectionManager().getSelectionCenter().setRotation(new Quaternion(0.1f,0.2f,0.5f,0.1f));
//                selectedCenter = base.getSelectionManager().getSelectionCenter();                
            attachSelectedToTransformParent();
//                transformTool.setLocalRotation(selectedCenter.getRotation().clone());
            isActive = true;
            result = true;

        }
//        }

        return result;
    }

    public void scaleAll() {
        pickedAxis = PickedAxis.scaleAll;
        attachSelectedToTransformParent();
        isActive = true;
    }

    protected void attachSelectedToTransformParent() {

        tranformParentNode.setLocalTransform(new Transform());  // clear previous transform
        tranformParentNode.setLocalTranslation(selectionTransformCenter.getTranslation().clone());
        tranformParentNode.setLocalRotation(selectionTransformCenter.getRotation().clone());

        // New node to compensate rotation of tranformParentNode
        ndParent1.setLocalRotation(tranformParentNode.getLocalRotation().clone());
        Quaternion rotNdParent2 = selectionTransformCenter.getRotation().clone();
        rotNdParent2.inverseLocal();
        ndParent2.setLocalRotation(rotNdParent2);

        Vector3f moveDeltaVec = new Vector3f().subtract(tranformParentNode.getLocalTranslation());
        List selectedList = base.getSelectionManager().getSelectionList();
        
        // Loop selected List
        for (Object ID : selectedList) {
            long id = (Long) ID;
            Spatial sp = base.getSpatialSystem().getSpatialControl(id).getGeneralNode();

            Object layerObj = sp.getParent().getUserData("LayerNumber");
            int layerNumb = (Integer) layerObj;

            String layersGroupName = (String) sp.getParent().getUserData("LayersGroupName");
            String sceneName = (String) sp.getParent().getUserData("SceneName");
            
            ndParent2.attachChild(sp);

            sp.setUserData("entityLayerNumber", layerNumb); //set entity's layer number
            sp.setUserData("entityLayersGroupName", layersGroupName); //set entity's layerGroup
            sp.setUserData("entitySceneName", sceneName); //set entity's scene
            
            // make transform magic
            sp.getLocalTranslation().addLocal(moveDeltaVec);
            ndParent1.setLocalRotation(new Quaternion());
            Transform tr = sp.getWorldTransform().clone();
            tranformParentNode.attachChild(sp);
            sp.getLocalTranslation().addLocal(moveDeltaVec);
            sp.setLocalTransform(tr);
            ndParent1.setLocalRotation(tranformParentNode.getLocalRotation().clone());

        }

        ndParent2.setLocalRotation(new Quaternion());
        ndParent1.setLocalRotation(new Quaternion());
    }

    private void detachSelectedFromTransformParent() {
        List selectedList = base.getSelectionManager().getSelectionList();

        for (Object ID : selectedList) {
            long id = (Long) ID;
            Spatial sp = base.getSpatialSystem().getSpatialControl(id).getGeneralNode();
            Transform tr = sp.getWorldTransform();
            Object layerObj = sp.getUserData("entityLayerNumber");
            int layerNumb = (Integer) layerObj;
            String layerGroup = (String) sp.getUserData("entityLayersGroupName");
            String sceneName = (String) sp.getUserData("entitySceneName");
            Node layer = base.getSceneManager().getScenesList().get(sceneName).getLayersGroupsList().get(layerGroup).getLayer(layerNumb); // get the layer
            layer.attachChild(sp);
            sp.setLocalTransform(tr);

            sp.setUserData("entityLayerNumber", null);
        }
    }

    public void deactivate() {
        if (pickedAxis != PickedAxis.None) {

            detachSelectedFromTransformParent();

            if (selectionTransformCenter != null) {
                base.getSelectionManager().calculateSelectionCenter();

                selectionTransformCenter = base.getSelectionManager().getSelectionCenter().clone();
                tranformParentNode.detachAllChildren();

                deltaMoveVector = null;  // clear deltaVector
                isActive = false;
                
                // SET HISTORY
                base.getHistoryManager().setNewSelectionHistory(base.getSelectionManager().getSelectionList());
                base.getHistoryManager().getHistoryList().get(base.getHistoryManager().getHistoryCurrentNumber()).setDoTransform(true);
            }
        }
        pickedAxis = PickedAxis.None;
    }

//    protected void setHistory() {
//        System.out.println("prepareTransHistory");
//        base.getHistoryManager().setNewTransformHistory(tranformParentNode.getWorldTransform().clone(), trCoordinates);
//        base.getHistoryManager().setNewSelectionHistory(base.getSelectionManager().getSelectionList());
//        EditorHistoryObject hisObject = base.getHistoryManager().getHistoryObject(base.getHistoryManager().getHistoryList().size() - 1);
//        hisObject.setDoTransform(true);
//        
////        // if objects are scaled
////        if ()
//    
//    }

//    // for HistoryManager only
//    protected void deactivateUndoRedo() {
//        detachSelectedFromTransformParent();
//        base.getSelectionManager().calculateSelectionCenter();
//        selectionTransformCenter = base.getSelectionManager().getSelectionCenter().clone();
//        tranformParentNode.detachAllChildren();
//    }

    public void updateTransformTool(){
        // update transform tool
        if (!isActive && base.getSelectionManager().getSelectionList().size() > 0
                && base.getSelectionManager().getSelectionCenter() != null) {

            //set Transform
            selectionTransformCenter = base.getSelectionManager().getSelectionCenter().clone();

            // set rotation for View and World Modes
            updateTransformCoords();


            // Update transform tool
            transformTool.detachAllChildren();
            if (transformType == transformType.MoveTool) {
                transformTool.attachChild(moveTool);
            } else if (transformType == transformType.RotateTool) {
                transformTool.attachChild(rotateTool);
            } else if (transformType == transformType.ScaleTool) {
                transformTool.attachChild(scaleTool);
            }
            updateTransformWidget(selectionTransformCenter);
//            System.out.println(selectionTransformCenter);
        } else if (base.getSelectionManager().getSelectionList().size() == 0) {
            transformTool.detachAllChildren();
        }
    }
    
    @Override
    public void update(float tpf) {

        // Transform Selected Objects!
        if (pickedAxis != PickedAxis.scaleAll && isActive && selectionTransformCenter != null) {
            if (transformType == transformType.MoveTool) {
                transformTool.detachAllChildren();
                moveToolObj.moveObjects();
            } else if (transformType == transformType.RotateTool) {
                transformTool.detachAllChildren();
                rotateToolObj.rotateObjects();
            } else if (transformType == transformType.ScaleTool) {
                transformTool.detachAllChildren();
                scaleToolObj.scaleObjects();
            }

        } // if scaleAll
        else if (isActive && selectionTransformCenter != null) {
            transformTool.detachAllChildren();
            scaleToolObj.scaleObjects();
        }
    }
}
