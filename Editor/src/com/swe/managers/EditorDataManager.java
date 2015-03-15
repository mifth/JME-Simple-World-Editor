/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.managers;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mifth
 */
public class EditorDataManager {

    private static ConcurrentHashMap<Long, ConcurrentHashMap<String, String>> entityDataList = new ConcurrentHashMap<Long, ConcurrentHashMap<String, String>>();

    public static ConcurrentHashMap<String, String> getEntityData(long ID) {
        return entityDataList.get(ID);
    }

    public static void setEntityData(long ID, ConcurrentHashMap<String, String> entityDataList) {
        EditorDataManager.entityDataList.put(ID, entityDataList);
    }    
    
    public static void removeEntityData(long ID) {
        EditorDataManager.entityDataList.remove(ID);
    }      

    public static void clearEntityData() {
        for (Long ID : entityDataList.keySet()) {
            entityDataList.get(ID).clear();
        }
        EditorDataManager.entityDataList.clear();
    }          
}
