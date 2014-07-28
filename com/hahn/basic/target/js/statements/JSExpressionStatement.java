package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSExpressionStatement extends ExpressionStatement {
    
    public JSExpressionStatement(Statement continer, BasicObject obj) {
        super(continer, obj);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return getObj().toTarget(builder);
    }
    
}
