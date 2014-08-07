package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.WhileStatement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSWhileStatement extends WhileStatement {
    
    public JSWhileStatement(Statement container, Node condition, Node body) {
        super(container, condition, body);
    }
    
    @Override
    public boolean endsWithBlock() {
        return true;
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        getInnerFrame().reverseOptimize();
        
        if (getConditionStatement() != null) {
            getConditionStatement().reverseOptimize();
        }
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        getInnerFrame().forwardOptimize();
        
        if (getConditionStatement() != null) {
            // Make sure don't free register before handling frame
            getConditionStatement().forwardOptimize();
        }
            
        return false;
    }
    
    @Override
    public String toTarget() {
        return JSPretty.format(0, "while(%s)%b", getConditionStatement(), getInnerFrame());
    }
}
