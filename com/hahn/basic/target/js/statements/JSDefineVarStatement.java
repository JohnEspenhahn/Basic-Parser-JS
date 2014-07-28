package com.hahn.basic.target.js.statements;

import java.util.List;

import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSDefineVarStatement extends DefineVarStatement {
    
    public JSDefineVarStatement(Statement container, boolean ignoreTypeCheck) {
        super(container, ignoreTypeCheck);
    }

    @Override
    public boolean useAddTargetCode() {
        return false;
    }

    @Override
    public String toTarget(LangBuildTarget builder) {
        List<DefinePair> pairs = getDefinePairs();
        
        boolean first = true;
        StringBuilder str = new StringBuilder();
        for (DefinePair pair: pairs) {
            if (!first) str.append(",");
            else first = false;
            
            str.append(String.format("%s=%s", pair.var.toTarget(builder), pair.val.toTarget(builder)));
        }
        
        return "var " + str;
    }
    
}
