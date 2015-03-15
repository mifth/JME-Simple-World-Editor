/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.transform;

/**
 *
 * @author mifth
 */
public class EditorTransformConstraint {

    private float moveConstraint, rotateConstraint, scaleConstraint;

    public EditorTransformConstraint() {
        moveConstraint = 0f;
        rotateConstraint = 0f;
        scaleConstraint = 0f;
    }

    public float constraintValue(float value, float constaintValue) {

        float theValue = value + 0f;
        float theConstraint = constaintValue + 0f;

        // fix
        if (constaintValue < 10f) {
            theValue *= 100f;
            theConstraint *= 100f;
        }

        if (theConstraint > 0f) {
            float rest = theValue % theConstraint;
            float valueToConstrait = theValue - rest;

            if (rest > theConstraint * 0.5f) {
                valueToConstrait += theConstraint;
            }

            // fix back
            if (constaintValue < 10f) {
                valueToConstrait /= 100f;
            }

            return valueToConstrait;

        } else {
            return value;
        }
    }

    public float getMoveConstraint() {
        return moveConstraint;
    }

    public void setMoveConstraint(float constraint) {
        this.moveConstraint = constraint;
    }

    public float getRotateConstraint() {
        return rotateConstraint;
    }

    public void setRotateConstraint(float rotateConstraint) {
        this.rotateConstraint = rotateConstraint;
    }

    public float getScaleConstraint() {
        return scaleConstraint;
    }

    public void setScaleConstraint(float scaleConstraint) {
        this.scaleConstraint = scaleConstraint;
    }
}
