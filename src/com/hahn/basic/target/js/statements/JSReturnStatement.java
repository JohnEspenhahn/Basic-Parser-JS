package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.statements.ReturnStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.js.JSPretty;

public class JSReturnStatement extends ReturnStatement {
    
    public JSReturnStatement(Statement container, FuncHead returnFrom, IBasicObject result) {
        super(container, returnFrom, result);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean doReverseOptimize() {
        getFile().pushCurrentLine(row);
        
        if (getResult() != null) {
            getResult().setInUse(this);
        }
        
        return false;
    }
    
    @Override
    public boolean doForwardOptimize() {
        getFile().pushCurrentLine(row);
        
        if (getResult() != null) {
            getResult().takeRegister(this);
        }
        
        return false;
    }
    
    @Override
    public String toTarget() {
        if (getResult() != null) {
            return JSPretty.format(getFile().isPretty(), 0, "return %s", getResult());
        } else {
            return "return";
        }
    }
    
}
