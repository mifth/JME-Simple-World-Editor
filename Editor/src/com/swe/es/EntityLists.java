/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.es;

import com.jme3.math.Vector3f;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author normenhansen
 */
public class EntityLists {

//    public static List<Entity> findComponent(List<Entity> entities, Class component) {
//        List<Entity> list = new LinkedList<Entity>();
//        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
//            Entity entity = it.next();
//            Object curComponent = entity.getComponent(component);
//            if (curComponent != null) {
//                list.add(entity);
//            }
//        }
//        return list;
//    }
//
//    public static List<Entity> findComponent(List<Entity> entities, Object component) {
//        List<Entity> list = new LinkedList<Entity>();
//        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
//            Entity entity = it.next();
//            Object curComponent = entity.getComponent(component.getClass());
//            if (curComponent != null && curComponent.equals(component)) {
//                list.add(entity);
//            }
//        }
//        return list;
//    }
//
//    public static List<Entity> filterComponent(List<Entity> entities, Class component) {
//        List<Entity> list = new LinkedList<Entity>();
//        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
//            Entity entity = it.next();
//            Object curComponent = entity.getComponent(component);
//            if (curComponent == null) {
//                list.add(entity);
//            }
//        }
//        return list;
//    }
//
//    public static List<Entity> filterComponent(List<Entity> entities, Object component) {
//        List<Entity> list = new LinkedList<Entity>();
//        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
//            Entity entity = it.next();
//            Object curComponent = entity.getComponent(component.getClass());
//            if (curComponent == null || !curComponent.equals(component)) {
//                list.add(entity);
//            }
//        }
//        return list;
//    }
//
//    public static List<Entity> findInRange(List<Entity> entities, Vector3f location, float distance) {
//        List<Entity> list = new LinkedList<Entity>();
//        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
//            Entity entity = it.next();
//            ComponentTransform curComponent = (ComponentTransform) entity.getComponent(ComponentTransform.class);
//            if (curComponent != null) {
//                if (location.subtract(curComponent.getLocation()).length() <= distance) {
//                    list.add(entity);
//                }
//            }
//        }
//        return list;
//    }
}
