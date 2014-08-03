package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class ReturnStatement extends Statement {
	private FuncHead func;
	private BasicObject result;
    
    public ReturnStatement(Statement container, FuncHead returnFrom, BasicObject result) {
        super(container);
        
        this.func = returnFrom;
        this.result = result;
    }
    
    public FuncHead getReturnFromFunc() {
    	return func;
    }
    
    public BasicObject getResult() {
    	return result;
    }
    
    @Override
    public final boolean reverseOptimize() {
        boolean result = doReverseOptimize();
        
        Type actual = getResult().getType();
        Type expected = getReturnFromFunc().getReturnType();
        if (!actual.doesExtend(expected)) {
            throw new CompileException("Invalid return type. Expected `" + expected + "` but got `" + actual + "`", this);
        }
        
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
