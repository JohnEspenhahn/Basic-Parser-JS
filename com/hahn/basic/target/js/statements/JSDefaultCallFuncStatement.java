package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.statements.Statement;

public class JSDefaultCallFuncStatement extends JSCallFuncStatement {
    
    public JSDefaultCallFuncStatement(Statement container, FuncCallPointer fcp) {
        super(container, fcp);
    }

    @Override
    protected boolean shouldCallFunction() {
        return !getFuncCallPointer().isUsed();
    }
    
}
