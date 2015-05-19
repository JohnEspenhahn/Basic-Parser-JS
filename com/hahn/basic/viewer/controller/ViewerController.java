package com.hahn.basic.viewer.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.hahn.basic.Main;
import com.hahn.basic.viewer.Viewer;
import com.hahn.basic.viewer.util.ViewerListener;

public class ViewerController implements ViewerListener {
    
    public void onViewerAction(Viewer view, EnumAction action) {
        switch(action) {
        case SAVE_AS:
            doSaveAsFile(view);
            break;
        case SAVE:
            doSaveFile(view);
            break;
        case OPEN:
            doOpenFile(view);
            break;
        case TOGGLE_DEBUG:
            doToggleDebug(view);
            break;
        case TOGGLE_PRETTY:
            doTogglePretty(view);
            break;
        }
    }
    
    private void doSaveAsFile(Viewer view) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            saveToFile(view, chooser.getSelectedFile(), false);
        } else {
            System.out.println("ERROR: Canceled by user");
        }
    }
    
    private void doSaveFile(Viewer view) {
        File f = Main.getInstance().getInputFile();
        if (f != null) saveToFile(view, f, true);
        else doSaveAsFile(view);
    }
    
    private void doOpenFile(Viewer view) {
        Main.getInstance().guiFileInput();
        view.markChanged();
    }
    
    private void doToggleDebug(Viewer view) {
        Main.getInstance().toggleDebug();
        view.markChanged();
    }
    
    private void doTogglePretty(Viewer view) {
        Main.getInstance().togglePretty();
    }
    
    private void saveToFile(Viewer view, File f, boolean forceReplace) {
        if (!f.exists() || f.isFile()) {
            if (f.isFile() && !forceReplace) {
                int replace = JOptionPane.showConfirmDialog(view, "The file '" + f.getName() + "' already exists. Replace it?", "Replace Existing File?", JOptionPane.YES_NO_OPTION);
                if (replace != JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(view, "Canceled");
                    return;
                }
            }
            
            FileOutputStream out = null;
            try {
                f.delete();
                f.createNewFile();                    
                out = new FileOutputStream(f);
                out.write(view.getSourceText().getBytes(Charset.forName("UTF-8")));
                
                view.setStatus("Saved");
            } catch (Exception err) {
                JOptionPane.showMessageDialog(view, String.format("Failed to save file! \n %s", err.toString()));
                
                err.printStackTrace();
            } finally {
                if (out != null) { 
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(view, "Invalid file selected. Save canceled");
        }
    }
    
}
