package com.hahn.basic.viewer.util;

import com.hahn.basic.viewer.Viewer;

public interface ViewerListener {
    void onViewerAction(Viewer view, EnumAction action);
    
    public enum EnumAction {
        SAVE_AS, SAVE, OPEN, TOGGLE_DEBUG, TOGGLE_PRETTY;
    }
}
