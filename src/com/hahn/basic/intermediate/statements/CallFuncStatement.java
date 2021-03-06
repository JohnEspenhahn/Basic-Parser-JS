package com.hahn.basic.intermediate.statements;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;

public abstract class CallFuncStatement extends Statement {    
    private FuncCallPointer funcCallPointer;
    
    public CallFuncStatement(Statement container, FuncCallPointer fcp) {
        super(container);
        
        this.funcCallPointer = fcp;
    }
    
    public FuncCallPointer getFuncCallPointer() {
        return funcCallPointer;
    }
    
    public IBasicObject[] getParams() {
        return funcCallPointer.getParams();
    }
    
    @Override
    public final boolean reverseOptimize() {
        getFile().pushCurrentLine(row);
        
    	if (shouldCallFunction()) {    	    
		    funcCallPointer.setInUse(this);
		    return doReverseOptimize();
    	} else {
    		return true;
    	}
    }
    
    public abstract boolean doReverseOptimize();
    
    @Override
    public final boolean forwardOptimize() {
        getFile().pushCurrentLine(row);
        funcCallPointer.takeRegister(this);
        
        return doForwardOptimize();
    }
    
    public abstract boolean doForwardOptimize();
    
    protected abstract boolean shouldCallFunction();
    
    @Override
    public String toString() {
        return FuncHead.toHumanReadable(funcCallPointer) + "(" + StringUtils.join(getParams(), ", ") + ")";
    }
}
