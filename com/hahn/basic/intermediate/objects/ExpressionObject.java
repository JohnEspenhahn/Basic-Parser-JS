package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.target.LangBuildTarget;

public abstract class ExpressionObject extends BasicObjectHolder {
    private boolean forcedGroup;
    
    public ExpressionObject(ExpressionStatement s) {
        super(s.getObj(), s.getObj().getType());
        
        this.forcedGroup = s.isForcedGroup();
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
    
    @Override
    public abstract String toTarget(LangBuildTarget builder);
}
