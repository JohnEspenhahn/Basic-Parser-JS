package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.ExpressionObject;
import com.hahn.basic.intermediate.statements.ExpressionStatement;

public class JSExpressionObject extends ExpressionObject {
    
    public JSExpressionObject(ExpressionStatement s) {
        super(s);
    }
    
    @Override
    public String toTarget() {
        return getHeldObject().toTarget();
    }
    
}
