/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swe.gui;

import com.swe.EditorBaseManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;

/**
 *
 * @author mifth
 */
public class LayerController implements Controller {

    private EditorBaseManager base = EditorBaseManager.getInstance();

    @Override
    public void bind(Nifty nifty, Screen screen, Element elmnt, Properties prprts, Attributes atrbts) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(Properties prprts, Attributes atrbts) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStartScreen() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onFocus(boolean bln) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean inputEvent(NiftyInputEvent nie) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @NiftyEventSubscriber(pattern = "layerBox.*")
    public void onClick(String id, NiftyMousePrimaryClickedEvent event) {
//        System.out.println("element with id [" + id + "] clicked at [" + event.getMouseX()
//                + ", " + event.getMouseY() + "]");
        if (!base.getEventManager().isActive()) {
            if (!base.getEventManager().isCtrlBool()) {
                base.getGuiManager().switchLayer(id.substring("layerBox_".length()));
            } else {
                base.getGuiManager().lockLayer(id.substring("layerBox_".length()));
            }
            
        }

    }
}
