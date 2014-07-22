package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.statements.CallFuncStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.Util;

public class JSCallFuncStatement extends CallFuncStatement {
    
    public JSCallFuncStatement(Statement container, FuncCallPointer fcp) {
        super(container, fcp);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean doReverseOptimize() {
        return false;
    }
    
    @Override
    public boolean doForwardOptimize() {
        return false;
    }
    
    @Override
    protected boolean shouldCallFunction() {
        return true;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        FuncCallPointer funccall = getFuncCallPointer();
        return funccall.getFuncId() + "(" + Util.toTarget(funccall.getParams(), ",", builder) + ")";
    }
    
}
