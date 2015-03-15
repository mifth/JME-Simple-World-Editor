/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.gui;

import com.swe.EditorBaseManager;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author mifth
 */
public abstract class EditorGuiAbstractChild {
    
    protected EditorBaseManager base;
    protected Screen screen;
    protected EditorGuiManager guiManager;
    protected ListBox entitiesListBox, layersGroupsObjectsListBox, componentsListBox, assetsListBox, scenesListbox, layersGroupsListbox;
    protected Element popupMoveToLayer, popupAddComponent, popupEditAsset;
    
    public EditorGuiAbstractChild(EditorBaseManager base) {
        this.base = base;
        guiManager = base.getGuiManager();
        screen = guiManager.getScreen();
        entitiesListBox = guiManager.getListBoxesList().get("entitiesListBox");
        layersGroupsObjectsListBox = guiManager.getListBoxesList().get("layersGroupObjectsListBox");
        componentsListBox = guiManager.getListBoxesList().get("componentsListBox");
        assetsListBox = guiManager.getListBoxesList().get("assetsListBox");
        scenesListbox = guiManager.getListBoxesList().get("scenesListbox");
        layersGroupsListbox = guiManager.getListBoxesList().get("layersGroupsListBox");
        
        popupMoveToLayer = guiManager.getPopup("popupMoveToLayer");
        popupAddComponent = guiManager.getPopup("popupAddComponent");
        popupEditAsset = guiManager.getPopup("popupEditAsset");
    }
    
}
