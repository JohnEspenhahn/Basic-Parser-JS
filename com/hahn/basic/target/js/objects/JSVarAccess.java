package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.VarAccess;
import com.hahn.basic.intermediate.objects.types.StructType.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;

public class JSVarAccess extends VarAccess {
    
    public JSVarAccess(Statement container, BasicObject var, BasicObject index, Type type, CodeFile file, int row, int col) {
        super(container, var, index, type, file, row, col);
    }
    
    @Override
    public String toTarget() {
        if (getAccessedAtIdx() instanceof StructParam) {
            return String.format("%s.%s", getAccessedWithinVar().toTarget(), getAccessedAtIdx().toTarget());
            
        // Indexing array or map
        } else {
            // g is alias for get function
            return String.format("%s.g(%s)", getAccessedWithinVar().toTarget(), getAccessedAtIdx().toTarget());
        }
    }
    
}
