package com.hahn.basic.viewer;

import javax.swing.JFrame;

public final class ViewerBuilder {
    
    private static Viewer view;
    
    public static void create() {
        if (view != null) return;
        
        //Create and set up the window.
        JFrame frame = new JFrame("Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        view = new Viewer(frame);
        frame.add(view);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
    }
    
    public static Viewer getViewer() {
        if (view == null) ViewerBuilder.create();
        
        return view;
    }
    
}
