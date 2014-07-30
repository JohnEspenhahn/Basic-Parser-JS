package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.ExpressionObject;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.target.LangBuildTarget;

public class JSExpressionObject extends ExpressionObject {
    
    public JSExpressionObject(ExpressionStatement s) {
        super(s);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return getHeldObject().toTarget(builder);
    }
    
}
