package com.swe;

import com.swe.camera.EditorCameraManager;
import com.swe.camera.SimpleChaseCamera;
import com.swe.transform.EditorTransformManager;


/**
 *
 * @author mifth
 */
public class EditorUpdateManager {
    
    private EditorCameraManager cameraManager;
    private EditorTransformManager transManager;

    public EditorUpdateManager(EditorCameraManager cameraManager ,EditorTransformManager transManager) {
        this.cameraManager = cameraManager;
        this.transManager = transManager;
    }
    
    public void doUpdate() {
        cameraManager.updateCamera();
        transManager.updateTransformTool();
    }
}
