package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.target.LangBuildTarget;

public abstract class ExpressionObject extends BasicObjectHolder {
    
    public ExpressionObject(ExpressionStatement s) {
        super(s.getObj(), s.getObj().getType());
    }
    
    @Override
    public abstract String toTarget(LangBuildTarget builder);
}
