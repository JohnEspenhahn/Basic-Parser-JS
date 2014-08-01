package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.target.LangBuildTarget;

public abstract class ExpressionObject extends BasicObjectHolder {
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
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        statement.forwardOptimize();
        
        super.takeRegister(by);
    }
    
    @Override
    public abstract String toTarget(LangBuildTarget builder);
}
