/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.transform;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.swe.EditorBaseManager;

/**
 *
 * @author mifth
 */
public class EditorTransformRotateTool {

    private AssetManager assetMan;
    private Node root, guiNode;
    private Application app;
    private EditorBaseManager base;
    private EditorTransformManager trManager;
    private Node collisionPlane;
    private EditorTransformConstraint constraintTool;

    public EditorTransformRotateTool(EditorTransformManager trManager, Application app, EditorBaseManager base, EditorTransformConstraint constraintTool) {

        this.app = app;
        this.base = base;
        assetMan = this.app.getAssetManager();
        root = (Node) this.app.getViewPort().getScenes().get(0);
        this.trManager = trManager;
        collisionPlane = trManager.getCollisionPlane();
        this.constraintTool = constraintTool;
    }

    protected void setCollisionPlane(CollisionResult colResult) {


        Transform selectedCenter = trManager.getselectionTransformCenter();

        // Set PickedAxis
        String type = colResult.getGeometry().getName();
        if (type.indexOf("rot_x") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.X);
        } else if (type.indexOf("rot_y") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.Y);
        } else if (type.indexOf("rot_z") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.Z);
        } 
        
//        else if (type.indexOf("rot_view") >= 0) {
//            trManager.setPickedAxis(EditorTransformManager.PickedAxis.View);
//        }

        EditorTransformManager.PickedAxis pickedAxis = trManager.getpickedAxis();

        // set the collision Plane location and rotation
        collisionPlane.setLocalTranslation(trManager.getselectionTransformCenter().getTranslation().clone());
        collisionPlane.getLocalRotation().lookAt(app.getCamera().getDirection(), Vector3f.UNIT_Y); //equals to angleZ

    }

    protected void rotateObjects() {

        // cursor position and selected position vectors
        Vector2f cursorPos = new Vector2f(app.getInputManager().getCursorPosition());
        Vector3f vectorScreenSelected = app.getCamera().getScreenCoordinates(base.getSelectionManager().getSelectionCenter().getTranslation());
        Vector2f selectedCoords = new Vector2f(vectorScreenSelected.getX(), vectorScreenSelected.getY());

        //set new deltaVector if it's not set
        if (trManager.getDeltaMoveVector() == null) {
            Vector2f deltaVecPos = new Vector2f(cursorPos.getX(), cursorPos.getY());
            Vector2f vecDelta = selectedCoords.subtract(deltaVecPos);
            trManager.setDeltaMoveVector(new Vector3f(vecDelta.getX(), vecDelta.getY(), 0));
        }



        Node trNode = trManager.getTranformParentNode();

        // Picked vector
        EditorTransformManager.PickedAxis pickedAxis = trManager.getpickedAxis();
        Vector3f pickedVec = Vector3f.UNIT_X;
        if (pickedAxis == EditorTransformManager.PickedAxis.Y) {
            pickedVec = Vector3f.UNIT_Y;
        } else if (pickedAxis == EditorTransformManager.PickedAxis.Z) {
            pickedVec = Vector3f.UNIT_Z;
        }


        // rotate according to angle
        Vector2f vec1 = selectedCoords.subtract(cursorPos).normalizeLocal();
        float angle = vec1.angleBetween(new Vector2f(trManager.getDeltaMoveVector().getX(), trManager.getDeltaMoveVector().getY()));
        angle = constraintTool.constraintValue(FastMath.RAD_TO_DEG * angle, constraintTool.getRotateConstraint()) * FastMath.DEG_TO_RAD;
        Quaternion rotationOfSelection = trManager.getselectionTransformCenter().getRotation();
        
        Vector3f axisToRotate = rotationOfSelection.mult(pickedVec);
        float angleCheck = axisToRotate.angleBetween(app.getCamera().getDirection());
        if (angleCheck > FastMath.HALF_PI) angle = -angle;
        
        Quaternion rot = rotationOfSelection.mult(rotationOfSelection.clone().fromAngleAxis(angle, pickedVec));
        
//            Quaternion newRotation = rotationOfSelection.mult(new Quaternion().fromAngleAxis(-angle, axisToRotate));
        trNode.setLocalRotation(rot);

//        System.out.println(trNode.getLocalRotation());
//            // rotate according to distance
//            Quaternion rotationOfSelection = base.getSelectionManager().getSelectionCenter().getRotation();
//            Vector3f axisToRotate = rotationOfSelection.mult(pickedVec).normalizeLocal();
//            Quaternion newRotation = rotationOfSelection.clone().fromAngleAxis(distance, axisToRotate);
//            trNode.setLocalRotation(newRotation);
    }
}
