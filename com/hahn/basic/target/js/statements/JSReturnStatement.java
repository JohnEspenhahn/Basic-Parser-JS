package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.statements.ReturnStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSReturnStatement extends ReturnStatement {
    
    public JSReturnStatement(Statement container, FuncHead returnFrom, BasicObject result) {
        super(container, returnFrom, result);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        getResult().setInUse(this);
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        if (getResult() instanceof AdvancedObject) {
            ((AdvancedObject) getResult()).takeRegister(this);
        }
        
        return false;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("return %s;", getResult());
    }
    
}