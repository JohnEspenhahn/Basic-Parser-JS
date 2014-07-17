package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.target.LangBuildTarget;

public abstract class Compilable {
    protected final int row;
    private final Frame frame;

    public Compilable(Frame f) {
        this.row = Main.getRow();

        this.frame = f;
    }

    public Frame getFrame() {
        return frame;
    }

    /**
     * First optimization iteration in reverse
     * @return True if should remove this object
     */
    public boolean reverseOptimize() {
        return false;
    }

    /**
     * Last optimization iteration forward
     * @return True if should remove this object
     */
    public boolean forwardOptimize() {
        return false;
    }

    /**
     * Final conversion to target language. 
     * Should NOT do any advanced optimization
     * @param code Compile to this code
     */
    public abstract void toTarget(LangBuildTarget builder);

    @Override
    public abstract String toString();
}
