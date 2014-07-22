package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.LangBuildTarget;

public abstract class Compilable implements IIntermediate, ILangCommand {
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
     * @param builder The build target
     * @return String for this build target
     */
    public abstract String toTarget(LangBuildTarget builder);

    @Override
    public abstract String toString();
}
