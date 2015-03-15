/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.events;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.swe.EditorBaseManager;
import com.swe.selection.EditorSelectionManager;

/**
 *
 * @author mifth
 */
public class EditorEventManager extends AbstractAppState {

    private boolean shiftBool, shiftCheck, ctrlBool, ctrlCheck, altBool, altCheck, action = false;
    private EditorBaseManager base;

    public EditorEventManager(EditorBaseManager base) {
        this.base = base;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
    }

    @Override
    public void update(float tpf) {

        if (shiftBool) {
            if (shiftCheck) {
                shiftBool = false;
                shiftCheck = false;
                base.getSelectionManager().setSelectionMode(EditorSelectionManager.SelectionMode.Normal);
            } else {
                shiftCheck = true;
            }

        }

        if (ctrlBool) {
            if (ctrlCheck) {
                ctrlBool = false;
                ctrlCheck = false;
                base.getSelectionManager().setSelectionTool(EditorSelectionManager.SelectionToolType.MouseClick);
            } else {
                ctrlCheck = true;
            }

        }

        if (altBool) {
            if (altCheck) {
                altBool = false;
                altCheck = false;
            } else {
                altCheck = true;
            }
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

    }

    @Override
    public void render(RenderManager rm) {
//        shiftBool = false;
//        ctrlBool = false;
//        altBool = false;
    }

    @Override
    public void postRender() {
//        shiftBool = false;
//        base.getSelectionManager().setSelectionMode(EditorSelectionManager.SelectionMode.Normal);
//
//        ctrlBool = false;
//        base.getSelectionManager().setSelectionTool(EditorSelectionManager.SelectionToolType.MouseClick);
//
//        altBool = false;
    }

    public void setAction(boolean newAction) {
        action = newAction;
    }

    public boolean isActive() {
        return action;
    }

    public boolean isShiftBool() {
        return shiftBool;
    }

    public void setShiftBool(boolean shiftBool) {
        this.shiftBool = shiftBool;
        shiftCheck = false;
    }

    public boolean isCtrlBool() {
        return ctrlBool;
    }

    public void setCtrlBool(boolean ctrlBool) {
        this.ctrlBool = ctrlBool;
        ctrlCheck = false;
    }

    public boolean isAltBool() {
        return altBool;
    }

    public void setAltBool(boolean altBool) {
        this.altBool = altBool;
        altCheck = false;
    }
}
