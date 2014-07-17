package com.hahn.basic.intermediate.statements.function;

import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.statements.Statement;

public class DefaultCallFuncStatement extends CallFuncStatement {
    
    public DefaultCallFuncStatement(Statement s, FuncCallPointer fcp) {
        super(s, fcp);
    }
    
    @Override
    public boolean reverseOptimize() {
        if (doCallPlaceholder()) {
            return super.reverseOptimize();
        } else {
            return true;
        }
    }
    
    @Override
    public boolean forwardOptimize() {
        if (doCallPlaceholder()) {
            return super.forwardOptimize();
        } else {
            return true;
        }
    }
    
    public boolean doCallPlaceholder() {
        return !getFuncCallPointer().isUsed();
    }
    
    @Override
    public String toString() {
        return "default " + super.toString();
    }
}
