package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.IFileObject;
import com.hahn.basic.target.Command;
import com.hahn.basic.target.CommandFactory;

public abstract class Compilable implements IIntermediate, IFileObject, Command {
    public final int row;
    private final Frame frame;

    public Compilable(Frame f, int row) {
        this.frame = f;
        this.row = row;
    }
    
    /**
     * @return Get the command factory associated with this object
     */
    @Override
    public CommandFactory getFactory() {
        return getCompiler().getFactory();
    }
    
    /**
     * @return The compiler associated with this object
     */
    public Compiler getCompiler() {
        return getFile().getCompiler();
    }

    /**
     * @return The file associated with this object
     */
    @Override
    public CodeFile getFile() {
        return getFrame().getFile();
    }
    
    /**
     * @return The frame associated with this object
     */
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
