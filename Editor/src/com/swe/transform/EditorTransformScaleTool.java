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
public class EditorTransformScaleTool {

    private AssetManager assetMan;
    private Node root, guiNode;
    private Application app;
    private EditorBaseManager base;
    private EditorTransformManager trManager;
    private Node collisionPlane;
    private Quaternion defaultRot = new Quaternion();
    private EditorTransformConstraint constraintTool;

    public EditorTransformScaleTool(EditorTransformManager trManager, Application app, EditorBaseManager base, EditorTransformConstraint constraintTool) {
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
        if (type.indexOf("scale_x") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.X);
        } else if (type.indexOf("scale_y") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.Y);
        } else if (type.indexOf("scale_z") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.Z);
        } 
//        else if (type.indexOf("scale_view") > 0) {
//            trManager.setPickedAxis(EditorTransformManager.PickedAxis.View);
//        }
        EditorTransformManager.PickedAxis pickedAxis = trManager.getpickedAxis();

        // set the collision Plane location and rotation
        collisionPlane.setLocalTranslation(selectedCenter.getTranslation().clone());
        collisionPlane.getLocalRotation().lookAt(app.getCamera().getDirection(), Vector3f.UNIT_Y); //equals to angleZ
    }

    protected void scaleObjects() {

        // cursor position and selected position vectors
        Vector2f cursorPos = new Vector2f(app.getInputManager().getCursorPosition());
        Vector3f vectorScreenSelected = app.getCamera().getScreenCoordinates(trManager.getselectionTransformCenter().getTranslation());
        Vector2f selectedCoords = new Vector2f(vectorScreenSelected.getX(), vectorScreenSelected.getY());

        //set new deltaVector if it's not set (scale tool stores position of a cursor)
        if (trManager.getDeltaMoveVector() == null) {
            Vector2f deltaVecPos = new Vector2f(cursorPos.getX(), cursorPos.getY());
//            Vector2f vecDelta = selectedCoords.subtract(deltaVecPos);
            trManager.setDeltaMoveVector(new Vector3f(deltaVecPos.getX(), deltaVecPos.getY(), 0));
        }



        Node trNode = trManager.getTranformParentNode();

        // Picked vector
        EditorTransformManager.PickedAxis pickedAxis = trManager.getpickedAxis();
        Vector3f pickedVec = Vector3f.UNIT_X;
        if (pickedAxis == EditorTransformManager.PickedAxis.Y) {
            pickedVec = Vector3f.UNIT_Y;
        } else if (pickedAxis == EditorTransformManager.PickedAxis.Z) {
            pickedVec = Vector3f.UNIT_Z;
        } else if (pickedAxis == EditorTransformManager.PickedAxis.scaleAll) {
            pickedVec = new Vector3f(1,1,1);
        }



        // scale according to distance
        Quaternion rotationOfSelection = trManager.getselectionTransformCenter().getRotation();
//            Vector3f axisToScale = rotationOfSelection.mult(pickedVec).normalize();
        Vector2f delta2d = new Vector2f(trManager.getDeltaMoveVector().getX(), trManager.getDeltaMoveVector().getY());
        Vector3f baseScale = new Vector3f(1, 1, 1); // default scale

        // scale object
        float disCursor = cursorPos.distance(selectedCoords);
        float disDelta = delta2d.distance(selectedCoords);
        Vector3f scalevec = null;
        float scaleValue = cursorPos.distance(delta2d);
        scaleValue = constraintTool.constraintValue(scaleValue *0.007f, constraintTool.getScaleConstraint());

        if (disCursor > disDelta) {
            scalevec = baseScale.add(pickedVec.mult(scaleValue));
//            System.out.println("xx");
        } else {
            scaleValue = Math.min(scaleValue, 0.999f); // remove negateve values
            scalevec = baseScale.subtract(pickedVec.mult((scaleValue)));
//            System.out.println("YY");
        }

//        System.out.println(scaleValue);
        trNode.setLocalScale(scalevec);



//        System.out.println(cursorPos.distance(delta2d));        
    }
}
