package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.util.Util;

public abstract class CallFuncStatement extends Statement {    
    private FuncCallPointer funcCallPointer;
    
    public CallFuncStatement(Statement container, FuncCallPointer fcp) {
        super(container);
        
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
    	    for (BasicObject o : getParams()) {
                o.setInUse(this);
            }
    	    
		    funcCallPointer.setInUse(this);
		    return super.reverseOptimize();
    	} else {
    		return true;
    	}
    }
    
    protected abstract boolean shouldCallFunction();
    
    @Override
    public String toString() {
        return FuncHead.toHumanReadable(funcCallPointer) + "(" + Util.toString(getParams(), ", ") + ")";
    }
}
