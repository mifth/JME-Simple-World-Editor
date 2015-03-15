package com.swe.scene;

import java.io.File;
import javax.swing.filechooser.*;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class EditorSceneFilter extends FileFilter{
    //Accept j3o and ogre xml files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

            if (f.getName().indexOf(".swe") > 0){
                    return true;
            } else {
                return false;
            }
    }
 
    //The description of this filter
    public String getDescription() {
        return "Scene (*.swe)";
    }
    
}
