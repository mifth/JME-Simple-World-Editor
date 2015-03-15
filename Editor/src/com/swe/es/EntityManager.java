package com.swe.es;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class EntityManager {

    private static Map<Long, ComponentsControl> entitiesData = new ConcurrentHashMap<Long, ComponentsControl>();
    private static AtomicLong idx;

    
    public EntityManager() {
        this(0);
    }
    
    public EntityManager(long initialValue) {
        this.idx = new AtomicLong(initialValue);
    }

    public long createEntity() {
        idx.getAndIncrement();
        setComponentControl(idx.get(), null);
        
        return idx.get();
    }

    public void setComponentControl(long ID, ComponentsControl components) {
        if (components == null) {
            ComponentsControl componentsNew = new ComponentsControl(ID);
            entitiesData.put(ID, componentsNew);
        } else {
            entitiesData.put(ID, components);
        }
    }

    public ComponentsControl getComponentControl(long ID) {
        return entitiesData.get(ID);
    }

    public boolean containsID(long ID) {
        return entitiesData.containsKey(ID);
    }

    public static Object getComponent(long ID, Class getClass) {
        return entitiesData.get(ID).getComponent(getClass);
    }

    private void removeComponentControl(long ID) {
        entitiesData.get(ID).clearComponents();
        entitiesData.remove(ID);
    }

    public long getIdx() {
        return idx.get();
    }

    public void setIdx(long id) {
        this.idx.set(id);
    }

    public Map<Long, ComponentsControl> getAllEntities() {
        return entitiesData;
    }

    public void removeEntity(long ID) {
        // remove entity
        removeComponentControl(ID);
    }
}
