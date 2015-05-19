package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.target.Command;

public abstract class Compilable implements IIntermediate, Command {
    public final int row;
    private final Frame frame;

    public Compilable(Frame f) {
        this.row = Main.getInstance().getRow();

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
     * and if statement
     */
    public boolean isBlock() {
        return false;
    }
    
    /**
     * Should be called from FORWARD_OPTIMIZE, or at least
     * POST-REVERSE_OPTIMIZE.
     * @return True if all eventualities of this compilable
     * have a return statement
     */
    public boolean hasReturn() {
        return false;
    }

    @Override
    public abstract String toString();
}
