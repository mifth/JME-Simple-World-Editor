/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.events;

import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.swe.EditorBaseManager;
import com.swe.camera.EditorCameraManager;
import com.swe.selection.EditorSelectionManager;
import com.swe.transform.EditorTransformManager;
import de.lessvoid.nifty.elements.Element;

public class EditorMappings implements AnalogListener, ActionListener {

    private Node root, camHelper;
    private Application app;
    private Camera camera;
    private EditorBaseManager base;
    private EditorCameraManager camMan;
    private boolean transformResult;
    private boolean selectResult;
    private String[] mappings;

    public EditorMappings(Application app, EditorBaseManager baseParts) {

        this.app = app;
        this.base = baseParts;
        root = (Node) this.app.getViewPort().getScenes().get(0);
        camHelper = (Node) root.getChild("camTrackHelper");
        camera = app.getCamera();
        camMan = baseParts.getCamManager();

        transformResult = false;
        selectResult = false;

        setupKeys();
    }

    private void setupKeys() {
        //Set up keys and listener to read it

        mappings = new String[]{
            "MoveCameraHelper",
            "MoveCameraHelperToSelection",
            "MoveOrSelect",
            "S_Key_Edt",
            "History",
            "ShowHideRightPanel",
            "SelectDeselectAll",
            "LeftShiftKey_Edt",
            "LeftCtrlKey_Edt",
            "LeftAltKey_Edt",
            "RightShiftKey_Edt",
            "RightCtrlKey_Edt",
            "RightAltKey_Edt",
            "DelecteSelectedEnt",
            "SelectAllKey",
            "W_Key_Edt",
            "E_Key_Edt",
            "R_Key_Edt",
            "EdtLeftView",
            "EdtFrontView",
            "EdtTopView",};

        app.getInputManager().addMapping("MoveCameraHelper", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addMapping("MoveCameraHelperToSelection", new KeyTrigger(KeyInput.KEY_C));
        app.getInputManager().addMapping("MoveOrSelect", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addMapping("S_Key_Edt", new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping("History", new KeyTrigger(KeyInput.KEY_Z));
//        app.getInputManager().addMapping("HistoryRedo", new KeyTrigger(KeyInput.KEY_X));
        app.getInputManager().addMapping("ShowHideRightPanel", new KeyTrigger(KeyInput.KEY_TAB));
        
        app.getInputManager().addMapping("LeftShiftKey_Edt", new KeyTrigger(KeyInput.KEY_LSHIFT));
        app.getInputManager().addMapping("LeftCtrlKey_Edt", new KeyTrigger(KeyInput.KEY_LCONTROL));
        app.getInputManager().addMapping("LeftAltKey_Edt", new KeyTrigger(KeyInput.KEY_LMENU));
        app.getInputManager().addMapping("RightShiftKey_Edt", new KeyTrigger(KeyInput.KEY_RSHIFT));
        app.getInputManager().addMapping("RightCtrlKey_Edt", new KeyTrigger(KeyInput.KEY_RCONTROL));
        app.getInputManager().addMapping("RightAltKey_Edt", new KeyTrigger(KeyInput.KEY_RMENU));
        
        app.getInputManager().addMapping("DelecteSelectedEnt", new KeyTrigger(KeyInput.KEY_DELETE));
        app.getInputManager().addMapping("SelectAllKey", new KeyTrigger(KeyInput.KEY_A));
        app.getInputManager().addMapping("W_Key_Edt", new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping("E_Key_Edt", new KeyTrigger(KeyInput.KEY_E));
        app.getInputManager().addMapping("R_Key_Edt", new KeyTrigger(KeyInput.KEY_R));
        app.getInputManager().addMapping("EdtLeftView", new KeyTrigger(KeyInput.KEY_NUMPAD3));
        app.getInputManager().addMapping("EdtFrontView", new KeyTrigger(KeyInput.KEY_NUMPAD1));
        app.getInputManager().addMapping("EdtTopView", new KeyTrigger(KeyInput.KEY_NUMPAD7));

        addListener();
    }

    public void addListener() {
        app.getInputManager().addListener(this, mappings);
    }

    public void removeListener() {
        app.getInputManager().removeListener(this);
    }

    public void onAnalog(String name, float value, float tpf) {

        // Move Camera
        if (name.equals("MoveCameraHelper")) {
            camMan.setDoMoveCamera(true);
        }

        
        // NOTE: ALT CTRL SHIFT are switched off in EventManager
        if (name.equals("LeftCtrlKey_Edt") || name.equals("RightCtrlKey_Edt")) {
            base.getEventManager().setCtrlBool(true);
            base.getSelectionManager().setSelectionTool(EditorSelectionManager.SelectionToolType.Rectangle);
        } else if (name.equals("LeftShiftKey_Edt") || name.equals("RightShiftKey_Edt")) {
            base.getEventManager().setShiftBool(true);
            base.getSelectionManager().setSelectionMode(EditorSelectionManager.SelectionMode.Additive);
        } else if (name.equals("LeftAltKey_Edt") || name.equals("RightAltKey_Edt")) {
            base.getEventManager().setAltBool(true);
        }

    }

    public void onAction(String name, boolean isPressed, float tpf) {

        // some keys
        if (!base.getEventManager().isActive()) {
            if (name.equals("DelecteSelectedEnt") && isPressed) {
                base.getGuiManager().removeSelectedButton();
            } else if (name.equals("SelectAllKey") && isPressed) {
                base.getGuiManager().selectAllButton();
            } else if (name.equals("MoveCameraHelperToSelection") && isPressed && base.getEventManager().isCtrlBool()) {
                base.getGuiManager().cloneSelectedButton();
            } else if (name.equals("S_Key_Edt") && isPressed) {
                if (base.getEventManager().isCtrlBool() && !base.getEventManager().isShiftBool()) {
                    base.getGuiManager().saveSceneButton();
                } else if (base.getEventManager().isCtrlBool() && base.getEventManager().isShiftBool()) {
                    base.getGuiManager().saveAsNewSceneButton();
                } else {
                    if (base.getSelectionManager().getSelectionList().size() > 0) {
                        base.getHistoryManager().prepareNewHistory();
                        base.getTransformManager().scaleAll();
                        transformResult = true;
                        base.getEventManager().setAction(true);
                    }
                }
            } else if (name.equals("MoveCameraHelperToSelection") && isPressed) {
                if (!transformResult && !selectResult) {
                    Transform selectionCenter = base.getSelectionManager().getSelectionCenter();
                    if (selectionCenter != null) {
                        base.getCamManager().getCamTrackHelper().setLocalTranslation(selectionCenter.getTranslation().clone());
                    }
                    selectionCenter = null;
                }

            } else if (name.equals("MoveCameraHelperToSelection") && isPressed) {
                if (base.getSelectionManager().getSelectionList().size() > 0) {
                    base.getSelectionManager().clearSelectionList();
                }
            } else if (name.equals("W_Key_Edt") && isPressed) {
                if (base.getEventManager().isShiftBool()) {
                    base.getTransformManager().setTrCoordinates(EditorTransformManager.TransformCoordinates.WorldCoords);
                } else if (base.getEventManager().isAltBool()) {
                    base.getGuiManager().clearTransform("Translation");
                } else {
                    base.getTransformManager().setTransformType(EditorTransformManager.TransformToolType.MoveTool);
                }

            } else if (name.equals("E_Key_Edt") && isPressed) {
                if (base.getEventManager().isShiftBool()) {
                    base.getTransformManager().setTrCoordinates(EditorTransformManager.TransformCoordinates.LocalCoords);
                } else if (base.getEventManager().isAltBool()) {
                    base.getGuiManager().clearTransform("Rotation");
                } else {
                    base.getTransformManager().setTransformType(EditorTransformManager.TransformToolType.RotateTool);
                }

            } else if (name.equals("R_Key_Edt") && isPressed) {
                if (base.getEventManager().isShiftBool()) {
                    base.getTransformManager().setTrCoordinates(EditorTransformManager.TransformCoordinates.ViewCoords);
                } else if (base.getEventManager().isAltBool()) {
                    base.getGuiManager().clearTransform("Scale");
                } else {
                    base.getTransformManager().setTransformType(EditorTransformManager.TransformToolType.ScaleTool);
                }

            } else if (name.equals("EdtLeftView") && isPressed) {
                if (base.getEventManager().isCtrlBool()) {
                    base.getCamManager().getChaseCam().setCameraNodeRotation(0f, -FastMath.HALF_PI);
                } else {
                    base.getCamManager().getChaseCam().setCameraNodeRotation(0f, FastMath.HALF_PI);
                }

            } else if (name.equals("EdtFrontView") && isPressed) {
                if (base.getEventManager().isCtrlBool()) {
                    base.getCamManager().getChaseCam().setCameraNodeRotation(0f, FastMath.PI);
                } else {
                    base.getCamManager().getChaseCam().setCameraNodeRotation(0f, 0f);
                }

            } else if (name.equals("EdtTopView") && isPressed) {
                if (base.getEventManager().isCtrlBool()) {
                    base.getCamManager().getChaseCam().setCameraNodeRotation(FastMath.HALF_PI, 0f);
                } else {
                    base.getCamManager().getChaseCam().setCameraNodeRotation(-FastMath.HALF_PI, 0f);
                }

            }
        }

        // Select or transformTool an entity
        if (name.equals("MoveOrSelect") && isPressed && !name.equals("S_Key_Edt")) {


            if (!base.getEventManager().isActive()) {

                // make transform
                base.getHistoryManager().prepareNewHistory();
                transformResult = base.getTransformManager().activate();

                // make selection
                if (!transformResult) {
                    selectResult = base.getSelectionManager().activate();
                }
            }

            base.getEventManager().setAction(true);

        } else if (name.equals("MoveOrSelect") && !isPressed) {
            if (transformResult) {
                base.getTransformManager().deactivate();
                transformResult = false;
            }
            if (selectResult) {
                base.getSelectionManager().deactivate();
                selectResult = false;
            }

            base.getEventManager().setAction(false);
            System.out.println("transform done");
        }


        // Undo/Redo
        if (name.equals("History") && isPressed
                && !base.getEventManager().isActive() && base.getEventManager().isCtrlBool()
                && !base.getEventManager().isShiftBool()) {
            base.getHistoryManager().historyUndo();

        } else if (name.equals("History") && isPressed
                && !base.getEventManager().isActive() && base.getEventManager().isCtrlBool()
                && base.getEventManager().isShiftBool()) {
            base.getHistoryManager().historyRedo();
        }

        if (name.equals("ShowHideRightPanel") && isPressed && !base.getEventManager().isActive()) {
            base.getGuiManager().getScreen().getFocusHandler().resetFocusElements();
            Element rightPanel = base.getGuiManager().getRightPanel();
            if (rightPanel.isVisible()) {
                rightPanel.hide();
            } else {
                rightPanel.show();
            }
        }
    }
}
