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
    public final boolean reverseOptimize() {
    	if (shouldCallFunction()) {
    	    for (BasicObject o : getParams()) {
                o.setInUse(this);
            }
    	    
		    funcCallPointer.setInUse(this);
		    return doReverseOptimize();
    	} else {
    		return true;
    	}
    }
    
    public abstract boolean doReverseOptimize();
    
    @Override
    public final boolean forwardOptimize() {
        return doForwardOptimize();
    }
    
    public abstract boolean doForwardOptimize();
    
    protected abstract boolean shouldCallFunction();
    
    @Override
    public String toString() {
        return FuncHead.toHumanReadable(funcCallPointer) + "(" + Util.toString(getParams(), ", ") + ")";
    }
}
