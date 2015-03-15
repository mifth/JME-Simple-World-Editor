package com.swe.zz_deprecated;

import com.jme3.app.Application;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class EditorMenuItems extends DeprecatedSwing {
    
    
   public EditorMenuItems() {
    
       
    }
    
    
    protected void menuButtonz() {
       
        final JMenuItem itemOpen = new JMenuItem("Open");
        menuTortureMethods.add(itemOpen);
        itemOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        final JMenuItem itemSave = new JMenuItem("Save");
        menuTortureMethods.add(itemSave);
        itemSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        final JMenuItem itemExit = new JMenuItem("Exit");
        menuTortureMethods.add(itemExit);
        itemExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                frame.dispose();
                app.stop();
            }
        });        
        
//        JButton loadDiffuseButton = new JButton("Load Diffuse Texture"); 
//        loadDiffuseButton.setSize(200, 20);
//        loadDiffuseButton.setPreferredSize(new Dimension(190, 20));
//        loadDiffuseButton.setVerticalTextPosition(AbstractButton.CENTER);
//        loadDiffuseButton.setHorizontalTextPosition(AbstractButton.LEADING); 
//        loadDiffuseButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//              if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) loadDiffuseTexture();
//                
//                
//            }
//        });         
//        optionPanel.add(loadDiffuseButton);          
//
//        JButton loadNormalButton = new JButton("Load Normal Texture"); 
//        loadNormalButton.setSize(200, 20);
//        loadNormalButton.setPreferredSize(new Dimension(190, 20));
//        loadNormalButton.setVerticalTextPosition(AbstractButton.CENTER);
//        loadNormalButton.setHorizontalTextPosition(AbstractButton.LEADING); 
//        loadNormalButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//              if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) loadNormalTexture();
//            }
//        });         
//        optionPanel.add(loadNormalButton);         
//        
//        optionPanel.add(new JToolBar.Separator());         
//        
//        
//        JButton RemoveSelectedModel = new JButton("Remove Selected Model"); 
//        RemoveSelectedModel.setSize(200, 20);
//        RemoveSelectedModel.setPreferredSize(new Dimension(190, 20));
//        RemoveSelectedModel.setVerticalTextPosition(AbstractButton.CENTER);
//        RemoveSelectedModel.setHorizontalTextPosition(AbstractButton.LEADING); 
//        RemoveSelectedModel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//
//        
//                if (((BBSceneGrid)app).selectedEntity != null || listEntity.getSelectedValue() != null){
//                modelGeo.clear();
//                listGeo.repaint();
//                
//                modelEntity.removeElement(((BBSceneGrid)app).selectedEntity);
//                listEntity.repaint();         
//                
//                ((BBSceneGrid)app).RemoveSelectedEntity();                 
//              }
//            }
//        });         
//        optionPanel.add(RemoveSelectedModel);         
//        
//        JButton clearScene = new JButton("Clear Scene"); 
//        clearScene.setSize(200, 20);
//        clearScene.setPreferredSize(new Dimension(190, 20));
//        clearScene.setVerticalTextPosition(AbstractButton.CENTER);
//        clearScene.setHorizontalTextPosition(AbstractButton.LEADING); 
//        clearScene.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                ((BBSceneGrid)app).ClearScene();
//                modelEntity.clear();
//                listEntity.repaint();
//                modelGeo.clear();
//                listGeo.repaint();
//            }
//        });         
//        optionPanel.add(clearScene);     
//        
//        
//        optionPanel.add(new JToolBar.Separator());         
//        
//        
//        
//        JButton Nor_Inv_X = new JButton("Normal InvertX"); 
//        Nor_Inv_X.setSize(200, 20);
//        Nor_Inv_X.setPreferredSize(new Dimension(190, 20));
//        Nor_Inv_X.setVerticalTextPosition(AbstractButton.CENTER);
//        Nor_Inv_X.setHorizontalTextPosition(AbstractButton.LEADING); 
//        Nor_Inv_X.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) 
//                    ((BBSceneGrid)app).setShaderParam("Nor_Inv_X", strLst);
//            }
//        });         
//        optionPanel.add(Nor_Inv_X); 
//        
//        JButton Nor_Inv_Y = new JButton("Normal InvertY"); 
//        Nor_Inv_Y.setSize(200, 20);
//        Nor_Inv_Y.setPreferredSize(new Dimension(190, 20));
//        Nor_Inv_Y.setVerticalTextPosition(AbstractButton.CENTER);
//        Nor_Inv_Y.setHorizontalTextPosition(AbstractButton.LEADING); 
//        Nor_Inv_Y.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) 
//                    ((BBSceneGrid)app).setShaderParam("Nor_Inv_Y", strLst);
//            }
//        });         
//        optionPanel.add(Nor_Inv_Y); 
//        
//        JButton Alpha_A_Dif = new JButton("Alpha Diffuse"); 
//        Alpha_A_Dif.setSize(200, 20);
//        Alpha_A_Dif.setPreferredSize(new Dimension(190, 20));
//        Alpha_A_Dif.setVerticalTextPosition(AbstractButton.CENTER);
//        Alpha_A_Dif.setHorizontalTextPosition(AbstractButton.LEADING); 
//        Alpha_A_Dif.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) 
//                    ((BBSceneGrid)app).setShaderParam("Alpha_A_Dif", strLst);
//            }
//        });         
//        optionPanel.add(Alpha_A_Dif); 
//        
//        JButton EmissiveMap = new JButton("Emissive Alpha Diffuse"); 
//        EmissiveMap.setSize(200, 20);
//        EmissiveMap.setPreferredSize(new Dimension(190, 20));
//        EmissiveMap.setVerticalTextPosition(AbstractButton.CENTER);
//        EmissiveMap.setHorizontalTextPosition(AbstractButton.LEADING); 
//        EmissiveMap.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) 
//                    ((BBSceneGrid)app).setShaderParam("EmissiveMap", strLst);
//            }
//        });         
//        optionPanel.add(EmissiveMap); 
//        
//        JButton Spec_A_Nor = new JButton("Specular Normal"); 
//        Spec_A_Nor.setSize(200, 20);
//        Spec_A_Nor.setPreferredSize(new Dimension(190, 20));
//        Spec_A_Nor.setVerticalTextPosition(AbstractButton.CENTER);
//        Spec_A_Nor.setHorizontalTextPosition(AbstractButton.LEADING); 
//        Spec_A_Nor.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) 
//                    ((BBSceneGrid)app).setShaderParam("Spec_A_Nor", strLst);
//            }
//        });         
//        optionPanel.add(Spec_A_Nor); 
//        
//        JButton Spec_A_Dif = new JButton("Specular Diffuse"); 
//        Spec_A_Dif.setSize(200, 20);
//        Spec_A_Dif.setPreferredSize(new Dimension(190, 20));
//        Spec_A_Dif.setVerticalTextPosition(AbstractButton.CENTER);
//        Spec_A_Dif.setHorizontalTextPosition(AbstractButton.LEADING); 
//        Spec_A_Dif.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (((BBSceneGrid)app).selectedEntity != null && strLst.size() > 0) 
//                    ((BBSceneGrid)app).setShaderParam("Spec_A_Dif", strLst);
//            }
//        });         
//        optionPanel.add(Spec_A_Dif); 
                
    }

    protected void panelButtonz(Container cnt){
        
        JButton loadModelButton = new JButton("Load Model"); 
        loadModelButton.setSize(200, 20);
        loadModelButton.setPreferredSize(new Dimension(190, 20));
        loadModelButton.setVerticalTextPosition(AbstractButton.CENTER);
        loadModelButton.setHorizontalTextPosition(AbstractButton.LEADING); 
        loadModelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                loadModelFromFile();
                
            }
        });         
        cnt.add(loadModelButton);  
        
        
//                JMenuItem loadModelButton = new JMenuItem("Load Model");
//        cnt.add(loadModelButton);
//        loadModelButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
////                    loadModelFromFile();
//            }
//        });
        
    }
    
    
//    private void loadModelFromFile(){
//        mFileCm.setFileFilter(modFilter);
//        int returnVal = mFileCm.showOpenDialog(null);
//        
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = mFileCm.getSelectedFile();
////            try{
////                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
////                ((BBSceneGrid)app).loadExternalModel(file.getName(), file.getParent());
////                
////                //Load Entity list
////
////                modelEntity.addElement(((BBSceneGrid)app).selectedEntity);
////                listEntity.repaint();
////                // Load Geometries list
////                modelGeo.clear();
////                
////                for (int i=0; i<BBWorldManager.getInstance().getEntity(((BBSceneGrid)app).selectedEntity).getAllGeometries().toArray().length; i++) {
////                    Geometry geo = BBWorldManager.getInstance().getEntity(((BBSceneGrid)app).selectedEntity).getAllGeometries().get(i);
////                    geo.setUserData("Model", file.getName());
////                    modelGeo.add(i, geo.getName());
////                    }                
////                
////                listGeo.repaint();
////                strLst.clear();
////                
////            }catch (IOException ex){}
//            
//        }
//        
//        mFileCm.setSelectedFile(null);
//    }

//    private void loadDiffuseTexture(){
//        mFileCm.setFileFilter(texFilter);
//        int returnVal = mFileCm.showOpenDialog(null);
//        
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = mFileCm.getSelectedFile();
////            mFileCt.setCurrentDirectory(file);
//            try{
//                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
//
//                if (strLst.size() > 0 && ((BBSceneGrid)app).selectedEntity != null) {
//
//                ((BBSceneGrid)app).loadTexture("DiffuseMap", file.getName(), file.getParent(), strLst);
//                }
//            }catch (IOException ex){}
//        }
//        
//        mFileCm.setSelectedFile(null);
//    }  
//    
//    private void loadNormalTexture(){
//        mFileCm.setFileFilter(texFilter);
//        int returnVal = mFileCm.showOpenDialog(null);
//        
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = mFileCm.getSelectedFile();
////            mFileCt.setCurrentDirectory(file);
//            try{
//                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
//                
//
//                if (strLst.size() > 0 && ((BBSceneGrid)app).selectedEntity != null) {
//                
//                ((BBSceneGrid)app).loadTexture("NormalMap", file.getName(), file.getParent(), strLst);
//                
//                }
//            }catch (IOException ex){}
//        }
//        
//        mFileCm.setSelectedFile(null);
//    }     
    
}

