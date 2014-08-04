package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.target.ILangCommand;

public abstract class Compilable implements IIntermediate, ILangCommand {
    public final int row;
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
     * @return true if ends with a block. For example,
     * in a javascript an if statement.
     */
    public boolean endsWithBlock() {
        return false;
    }

    @Override
    public abstract String toString();
}
