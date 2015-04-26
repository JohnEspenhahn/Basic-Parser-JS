package com.hahn.basic.target.js.objects;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.VarAccess;
import com.hahn.basic.intermediate.objects.types.StructType.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;

public class JSVarAccess extends VarAccess {
    
    public JSVarAccess(Statement container, BasicObject var, BasicObject index, Type type, int row, int col) {
        super(container, var, index, type, row, col);
    }
    
    @Override
    public String toTarget() {
        if (getIndex() instanceof StructParam) {
            return String.format("%s.%s", getVar().toTarget(), getIndex().toTarget());
        } else {
            return String.format("%s(%s,%s)", EnumToken.___g, getVar().toTarget(), getIndex().toTarget());
        }
    }
    
}
