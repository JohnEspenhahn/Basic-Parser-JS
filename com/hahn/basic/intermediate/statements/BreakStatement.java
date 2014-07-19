package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.util.CompileException;

public abstract class BreakStatement extends Statement {
    private Frame loop;
    
    public BreakStatement(Statement s) {
        super(s);
        
        loop = getFrame().getLoop();
        if (loop == null) {
            throw new CompileException("Invalid use of `break`");
        }
    }
    
    public Frame getLoop() {
    	return loop;
    }
    
    @Override
    public String toString() {
        return "break";
    }

}
