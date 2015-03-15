/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.es;

import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mifth
 */
public final class EntitySpatialsControl {

    private Spatial spatial;
    private static List<Geometry> mapChildMeshes = new ArrayList<Geometry>(); //Collection of meshes
    private SpatialType type;
//    private static EntityManager entManager;
    private long ID;
    private ComponentsControl components;

    public EntitySpatialsControl(Spatial sp, long ID, ComponentsControl components) {

        this.ID = ID;
        this.components = components;
        spatial = sp;
        recurseNodeID(spatial);
    }

    public enum SpatialType {

        Node,
        LightNode,
        BatchNode,
        CameraNode
    }

    public void setType(SpatialType type) {
        this.type = type;
    }

    public SpatialType getType() {
        return type;
    }

    public void setGeneralNode(Spatial sp) {
        Transform tr = spatial.getLocalTransform();
        Node layer = spatial.getParent();
        
        spatial.removeFromParent();
        spatial = sp;
        recurseNodeID(spatial);
        spatial.setLocalTransform(tr);
        if (layer != null) {
            layer.attachChild(spatial);
        }
    }

    public Spatial getGeneralNode() {
        return spatial;
    }

    //Read the node child to find geomtry and stored it to the map for later access as submesh
    private void recurseNodeID(Spatial generalSp) {
        generalSp.setUserData("EntityID", ID);
        
        if (mapChildMeshes.size() > 0) {
            mapChildMeshes.clear();
        }

        // set user data to children recusively
        if (generalSp instanceof Node) {
            Node nd = (Node) generalSp;
            
            for (Spatial sp : nd.getChildren()) {

                if (sp instanceof Node) {
                    sp.setUserData("EntityID", ID);
                    recurseNodeID((Node) sp);
                } else if (sp instanceof Geometry) {
                    Geometry geom = (Geometry) sp;
                    geom.setUserData("EntityID", ID);
                    mapChildMeshes.add(geom);
                }
            }
        }

    }

    public Geometry getChildMesh(String name) {
        for (Geometry mc : mapChildMeshes) {
            if (name.equals(mc.getName())) {
                return mc;
            }
        }
        return null;
    }

    public List<Geometry> getChildMeshes() {
        return mapChildMeshes;
    }

    public void destroy() {
        // CHILDREN CLEAN
        mapChildMeshes.clear();

        // remove all controls
        for (int i = 0; i < spatial.getNumControls(); i++) {
            spatial.removeControl(spatial.getControl(i));
        }

        spatial.removeFromParent();
        spatial = null;
    }
}
