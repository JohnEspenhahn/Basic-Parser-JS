package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.IBasicObject;

public abstract class ReturnStatement extends Statement {
	private FuncHead func;
	private IBasicObject result;
    
    public ReturnStatement(Statement container, FuncHead returnFrom, IBasicObject result) {
        super(container);
        
        this.func = returnFrom;
        this.result = result;
    }
    
    public FuncHead getReturnFromFunc() {
    	return func;
    }
    
    public IBasicObject getResult() {
    	return result;
    }
    
    @Override
    public final boolean reverseOptimize() {
        boolean result = doReverseOptimize();
        
        getFrame().flagHasReturn();
        
        return result;
    }
    
    @Override
    public final boolean forwardOptimize() {
        return doForwardOptimize();
    }
    
    public abstract boolean doReverseOptimize();
    public abstract boolean doForwardOptimize();
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof ReturnStatement) {
            ReturnStatement r = (ReturnStatement) o;
            return func.equals(r.func);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "DoReturn";
    }
}
