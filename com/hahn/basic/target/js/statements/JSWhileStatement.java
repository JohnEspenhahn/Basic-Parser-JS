package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.WhileStatement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;

public class JSWhileStatement extends WhileStatement {
    
    public JSWhileStatement(Statement container, Node condition, Node body) {
        super(container, condition, body);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        getInnerFrame().reverseOptimize();
        
        getConditionObject().setInUse(this);
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        getInnerFrame().forwardOptimize();
        
        if (getConditionObject() != null) {
            // Make sure don't free register before handling frame
            getConditionObject().takeRegister(this);
        }
            
        return false;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("while(%s){%s}", 
                getConditionObject().toTarget(builder), 
                getInnerFrame().toTarget(builder));
    }
}
