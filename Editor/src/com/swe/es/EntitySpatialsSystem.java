/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.es;

import com.swe.es.ComponentsControl;
import com.jme3.scene.Spatial;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mifth
 */
public final class EntitySpatialsSystem {

    private static Map<Long, EntitySpatialsControl> spatialsControls;

    public EntitySpatialsSystem() {
        spatialsControls = new ConcurrentHashMap<Long, EntitySpatialsControl>();
    }

    public EntitySpatialsControl setSpatialControl(Spatial sp, long ID, ComponentsControl control) {
        if (spatialsControls.get(ID) != null) {
            return null;
        }

        EntitySpatialsControl spControl = new EntitySpatialsControl(sp, ID, control);
        spatialsControls.put(ID, spControl);
        return spControl;
    }

    public EntitySpatialsControl getSpatialControl(long ID) {
        return spatialsControls.get(ID);
    }

    public void removeSpatialControl(long ID) {
        spatialsControls.get(ID).destroy();
        spatialsControls.remove(ID);
    }
    
    public void clearSpatialsSystem(){
        for (Long entId : spatialsControls.keySet()){
            spatialsControls.get(entId).destroy();
        }
        spatialsControls.clear();
    }
}
