package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.VarAccess;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSVarAccess extends VarAccess {
    
    public JSVarAccess(Statement container, BasicObject var, BasicObject index, Type type) {
        super(container, var, index, type);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("%s[%s]", getHeldObject().toTarget(builder), getIndex().toTarget(builder));
    }
    
}
