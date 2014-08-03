package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.statements.ExpressionStatement;

public abstract class ExpressionObject extends ObjectHolder {
    private ExpressionStatement statement;
    private boolean forcedGroup;
    
    public ExpressionObject(ExpressionStatement s) {
        super(s.getObj(), s.getObj().getType());
        
        this.forcedGroup = s.isForcedGroup();
        this.statement = s;
    }
    
    @Override
    public boolean isGrouped() {
        return super.isGrouped() || forcedGroup;
    }
    
    public void setForcedGroup(boolean b) {
        this.forcedGroup = b;
    }
    
    public boolean isForcedGroup() {
        return forcedGroup;
    }
    
    public ExpressionStatement getStatement() {
        return statement;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        statement.reverseOptimize();
        
        return super.isLastUse(this);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        statement.forwardOptimize();
    }
    
    @Override
    public abstract String toTarget();
}
