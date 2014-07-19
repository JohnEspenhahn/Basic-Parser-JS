package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.util.Util;

public abstract class CallFuncStatement extends Statement {    
    private FuncCallPointer funcCallPointer;
    
    public CallFuncStatement(Statement s, FuncCallPointer fcp) {
        super(s);
        
        this.funcCallPointer = fcp;
    }
    
    public FuncCallPointer getFuncCallPointer() {
        return funcCallPointer;
    }
    
    public BasicObject[] getParams() {
        return funcCallPointer.getParams();
    }
    
    @Override
    public boolean reverseOptimize() {
    	if (shouldCallFunction()) {
		    funcCallPointer.setInUse();
		    return super.reverseOptimize();
    	} else {
    		return true;
    	}
    }
    
    protected abstract boolean shouldCallFunction();
    
    @Override
    public abstract void addTargetCode();
    
    @Override
    public String toString() {
        return FuncHead.toHumanReadable(funcCallPointer) + "(" + Util.toString(getParams(), ", ") + ")";
    }
}
