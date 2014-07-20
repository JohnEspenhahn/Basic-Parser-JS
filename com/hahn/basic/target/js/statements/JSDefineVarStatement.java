package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.Statement;

public class JSDefineVarStatement extends DefineVarStatement {
    
    public JSDefineVarStatement(Statement container, BasicObject var, BasicObject val, boolean ignoreTypeCheck) {
        super(container, var, val, ignoreTypeCheck);
    }
    
}
