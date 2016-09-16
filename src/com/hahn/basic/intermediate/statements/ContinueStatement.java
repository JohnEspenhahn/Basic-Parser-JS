package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class ContinueStatement extends Statement {
    private Frame loop;
    
    public ContinueStatement(Statement s) {
        super(s);
        
        loop = getFrame().getLoop();
        if (loop == null) {
            throw new CompileException("Invalid use of `continue` outside a loop", this);
        }
    }

    @Override
    public String toString() {
        return "continue";
    }
}
