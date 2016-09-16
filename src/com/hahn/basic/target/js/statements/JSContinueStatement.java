package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.statements.ContinueStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.js.JSPretty;

public class JSContinueStatement extends ContinueStatement {
    
    public JSContinueStatement(Statement s) {
        super(s);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        return false;
    }
    
    @Override
    public String toTarget() {
        return JSPretty.format(0, "continue");
    }
    
}
