/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.es;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mifth
 */
public class ComponentsControl {
    
    private Map <Class<?>, EntityComponent> components = new ConcurrentHashMap <Class<?>, EntityComponent>();        
    private long ID;

    
    public ComponentsControl(long ID) {
        this.ID = ID;
    }
    
    public long getEntityID(ComponentsControl compControl) {
        return ID;
    }
    
    public EntityComponent getComponent(Class controlType) {
        return components.get(controlType);
    }

    public void setComponent(EntityComponent comp) {
         components.put(comp.getClass(), comp);
    }
    
    public void removeComponent(Class componentType){
        components.remove(componentType);
    }
    
    public void clearComponents() {
        components.clear();
    }    
    
    
}
