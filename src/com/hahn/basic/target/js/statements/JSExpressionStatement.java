package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.js.JSPretty;

public class JSExpressionStatement extends ExpressionStatement {
    
    public JSExpressionStatement(Statement continer, IBasicObject obj) {
        super(continer, obj);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public String toTarget() {
        return JSPretty.format(0, "%s", getObj().toTarget());
    }
    
}
