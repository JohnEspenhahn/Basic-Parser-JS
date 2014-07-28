package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.ExpressionStatementObject;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.target.LangBuildTarget;

public class JSExpressionStatementObject extends ExpressionStatementObject {
    
    public JSExpressionStatementObject(ExpressionStatement s) {
        super(s);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return getStatement().toTarget(builder);
    }
    
}
