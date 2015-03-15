/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.transform;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.swe.EditorBaseManager;
import com.swe.transform.EditorTransformManager.PickedAxis;

/**
 *
 * @author mifth
 */
public class EditorTransformMoveTool {

    private AssetManager assetMan;
    private Node root, guiNode;
    private Application app;
    private EditorBaseManager base;
    private EditorTransformManager trManager;
    private Node collisionPlane;
    private EditorTransformConstraint constraintTool;

    public EditorTransformMoveTool(EditorTransformManager trManager, Application app, EditorBaseManager base, EditorTransformConstraint constraintTool) {
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
        if (type.indexOf("move_x") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.X);
        } else if (type.indexOf("move_y") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.Y);
        } else if (type.indexOf("move_z") >= 0) {
            trManager.setPickedAxis(EditorTransformManager.PickedAxis.Z);
        }
//        else if (type.indexOf("move_view") > 0) {
//            trManager.setPickedAxis(EditorTransformManager.PickedAxis.View);
//        }
        PickedAxis pickedAxis = trManager.getpickedAxis();

        // select an angle between 0 and 90 degrees (from 0 to 1.57 in radians) (for collisionPlane)
        float angleX = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_X));
        if (angleX > 1.57) {
            angleX = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_X).negateLocal());
        }

        float angleY = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Y));
        if (angleY > 1.57) {
            angleY = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Y).negateLocal());
        }

        float angleZ = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Z));
        if (angleZ > 1.57) {
            angleZ = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Z).negateLocal());
        }

        //select the less angle for collisionPlane
        float lessAngle = angleX;
        if (lessAngle > angleY) {
            lessAngle = angleY;
        }
        if (lessAngle > angleZ) {
            lessAngle = angleZ;
        }

        // set the collision Plane location and rotation
        collisionPlane.setLocalTranslation(selectedCenter.getTranslation().clone());
        collisionPlane.setLocalRotation(selectedCenter.getRotation().clone()); //equals to angleZ
        Quaternion planeRot = collisionPlane.getLocalRotation();

        // rotate the plane for constraints
        if (lessAngle == angleX) {
//            System.out.println("XXXAngle");

            if (pickedAxis == PickedAxis.X && angleY > angleZ) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.X && angleY < angleZ) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Y) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
            } else if (pickedAxis == PickedAxis.Z) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            }
        } else if (lessAngle == angleY) {
            if (pickedAxis == PickedAxis.X) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Y && angleX < angleZ) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
            } else if (pickedAxis == PickedAxis.Z) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
            }
        } else if (lessAngle == angleZ) {
            if (pickedAxis == PickedAxis.X) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Z && angleY < angleX) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
            } else if (pickedAxis == PickedAxis.Z && angleY > angleX) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            }
        }
    }

    protected void moveObjects() {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray();
        Vector3f pos = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0f).clone();
        Vector3f dir = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 1f).clone();
        dir.subtractLocal(pos).normalizeLocal();
        ray.setOrigin(pos);
        ray.setDirection(dir);
        collisionPlane.collideWith(ray, results);
        CollisionResult result = results.getClosestCollision();

        // Complex trigonometry formula based on sin(angle)*distance
        if (results.size() > 0) {

            Vector3f contactPoint = result.getContactPoint(); // get a point of collisionPlane
            Transform selectedCenter = trManager.getselectionTransformCenter();

            //set new deltaVector if it's not set
            if (trManager.getDeltaMoveVector() == null) {
                trManager.setDeltaMoveVector(selectedCenter.getTranslation().subtract(contactPoint));
            }

            contactPoint = contactPoint.add(trManager.getDeltaMoveVector()); // add delta of the picked place

            Vector3f vec1 = contactPoint.subtract(selectedCenter.getTranslation());
            float distanceVec1 = selectedCenter.getTranslation().distance(contactPoint);

            // Picked vector
            PickedAxis pickedAxis = trManager.getpickedAxis();
            Vector3f pickedVec = Vector3f.UNIT_X;
            if (pickedAxis == EditorTransformManager.PickedAxis.Y) {
                pickedVec = Vector3f.UNIT_Y;
            } else if (pickedAxis == EditorTransformManager.PickedAxis.Z) {
                pickedVec = Vector3f.UNIT_Z;
            }
            // the main formula for constraint axis
            float angle = vec1.clone().normalizeLocal().angleBetween(selectedCenter.getRotation().mult(pickedVec).normalizeLocal());
            float distanceVec2 = distanceVec1 * FastMath.sin(angle);

            // fix if angle>90 degrees
            Vector3f perendicularVec = collisionPlane.getLocalRotation().mult(Vector3f.UNIT_X).mult(distanceVec2);
            Vector3f checkVec = contactPoint.add(perendicularVec).subtractLocal(contactPoint).normalizeLocal();
            float angleCheck = checkVec.angleBetween(vec1.clone().normalizeLocal());
            if (angleCheck < FastMath.HALF_PI) {
                perendicularVec.negateLocal();
            }


            // find distance to mave
            float distanceToMove = contactPoint.add(perendicularVec).distance(selectedCenter.getTranslation());

            distanceToMove = constraintTool.constraintValue(distanceToMove, constraintTool.getMoveConstraint());
            
            // invert value if it's needed for negative movement
            if (angle > FastMath.HALF_PI) {
                distanceToMove = -distanceToMove;
            }


            translateObjects(distanceToMove, pickedAxis, trManager.getTranformParentNode(), selectedCenter);
//            System.out.println("Vec: " + selectedCenter.getTranslation().toString() + "   angle: " + angle);
        }
    }

    private void translateObjects(float distance, PickedAxis pickedAxis, Node tranformParentNode, Transform selectedCenter) {

        tranformParentNode.setLocalTranslation(selectedCenter.getTranslation().clone());

        if (pickedAxis == PickedAxis.X) {
            tranformParentNode.getLocalTranslation().addLocal(selectedCenter.getRotation().getRotationColumn(0).mult(distance));
        } else if (pickedAxis == PickedAxis.Y) {
            tranformParentNode.getLocalTranslation().addLocal(selectedCenter.getRotation().getRotationColumn(1).mult(distance));
        } else if (pickedAxis == PickedAxis.Z) {
            tranformParentNode.getLocalTranslation().addLocal(selectedCenter.getRotation().getRotationColumn(2).mult(distance));
        }

    }
}
