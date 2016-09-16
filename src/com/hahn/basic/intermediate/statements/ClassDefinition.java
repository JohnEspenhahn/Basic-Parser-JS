package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.ClassType;

public abstract class ClassDefinition extends Statement {
    private ClassType type;
    
    public ClassDefinition(Frame containingFrame, ClassType type) {
        super(containingFrame);
        
        this.type = type;
    }
    
    public ClassType getClassType() {
        return type;
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        return false;
    }
    
    @Override
    public String toString() {
        return "define class " + getClassType().toString();
    }
    
}
