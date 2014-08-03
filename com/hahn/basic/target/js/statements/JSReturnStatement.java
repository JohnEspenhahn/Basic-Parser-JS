package com.hahn.basic.target.js.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.statements.ReturnStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.js.JSPretty;

public class JSReturnStatement extends ReturnStatement {
    
    public JSReturnStatement(Statement container, FuncHead returnFrom, BasicObject result) {
        super(container, returnFrom, result);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean doReverseOptimize() {
        Main.setLine(row);
        
        getResult().setInUse(this);
        
        return false;
    }
    
    @Override
    public boolean doForwardOptimize() {
        Main.setLine(row);
        
        getResult().takeRegister(this);
        
        return false;
    }
    
    @Override
    public String toTarget() {
        return JSPretty.format(0, "return %s", getResult());
    }
    
}
