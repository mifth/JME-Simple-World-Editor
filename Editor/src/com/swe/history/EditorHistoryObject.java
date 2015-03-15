/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.history;

import com.jme3.math.Transform;
import com.swe.transform.EditorTransformManager.TransformCoordinates;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mifth
 */
public class EditorHistoryObject {

    private ConcurrentHashMap<Long, Boolean> addDeleteEntitiesList;
    
    private  ConcurrentHashMap<Long, Transform> selectDeselectEntitiesList;
    private  SelectionHistory selectionChanges;
    
    private Transform tranformOfParentNode;
//    private Transform transformOfselectionTransformCenter;
//    private EditorTransformManager.TransformCoordinates transformCoords;
    private boolean doTransform;

//    private  ConcurrentHashMap<Long, ConcurrentHashMap<String, String>> entitiesDataCoponentsList;
//    private DataComponentsHistory dataComponentsChanges;

    
    
    protected enum SelectionHistory {

        Select, Deselect, None
    };

    protected enum DataComponentsHistory {

        AddDataComponents, RemoveDataComponents, None
    };

    
    public EditorHistoryObject() {
        selectDeselectEntitiesList = null;
        
        addDeleteEntitiesList = new ConcurrentHashMap<Long, Boolean>();
        selectionChanges = SelectionHistory.None;
        
       tranformOfParentNode = null;
//       transformOfselectionTransformCenter = null;
//       transformCoords = null;
       doTransform = false;
    
//        entitiesDataCoponentsList = new ConcurrentHashMap<Long, ConcurrentHashMap<String, String>>();
//        dataComponentsChanges = DataComponentsHistory.None;
    }


    protected void clearHistoryObject() {
        addDeleteEntitiesList.clear();

        if (selectDeselectEntitiesList != null) {
            selectDeselectEntitiesList.clear();
            selectDeselectEntitiesList = null;
        }
        selectionChanges = SelectionHistory.None;

        tranformOfParentNode = null;
//        transformOfselectionTransformCenter = null;

//        entitiesDataCoponentsList.clear();
//        dataComponentsChanges = DataComponentsHistory.None;        
    }

    
    
    protected  ConcurrentHashMap<Long, Boolean> getAddDeleteEntitiesList() {
        return addDeleteEntitiesList;
    }

//    protected  SceneHistory getSceneChanges() {
//        return sceneChanges;
//    }

//    protected  void setSceneChanges(SceneHistory sceneChanges) {
//        sceneChanges = sceneChanges;
//    }
    protected void createSelectDeselectEntitiesList() {
        selectDeselectEntitiesList = new ConcurrentHashMap<Long, Transform>();
    }
    protected  ConcurrentHashMap<Long, Transform> getSelectDeselectEntitiesList() {
        return selectDeselectEntitiesList;
    }

    protected  SelectionHistory getSelectionChanges() {
        return selectionChanges;
    }

//    protected  void setSelectionChanges(SelectionHistory selectionChanges) {
//        selectionChanges = selectionChanges;
//    }

    protected Transform getTransformOfParentNode() {
        return tranformOfParentNode;
    }

    protected void setTransformOfParentNode(Transform transformSelected) {
        this.tranformOfParentNode = transformSelected;
    }

//    protected Transform getTransformOfselectionTransformCenter() {
//        return transformOfselectionTransformCenter;
//    }

//    protected void setTransformOfselectionTransformCenter(Transform transformOfselectionTransformCenter) {
//        this.transformOfselectionTransformCenter = transformOfselectionTransformCenter;
//    }
    
//    protected TransformCoordinates getTransformCoords() {
//        return transformCoords;
//    }
//
//    protected void setTransformCoords(TransformCoordinates transformCoords) {
//        this.transformCoords = transformCoords;
//    }    
//
    protected boolean isDoTransform() {
        return doTransform;
    }

    public void setDoTransform(boolean doTransform) {
        this.doTransform = doTransform;
    }    
    
//    protected  ConcurrentHashMap<Long, ConcurrentHashMap<String, String>> getEntitiesCoponents() {
//        return entitiesDataCoponentsList;
//    }
//
//    protected DataComponentsHistory getComponentsChanges() {
//        return dataComponentsChanges;
//    }
//
//    protected void setComponentsChanges(DataComponentsHistory componentsChanges) {
//        this.dataComponentsChanges = componentsChanges;
//    }
}
