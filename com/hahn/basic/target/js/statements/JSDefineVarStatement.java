package com.hahn.basic.target.js.statements;

import java.util.List;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.exceptions.CompileException;

public class JSDefineVarStatement extends DefineVarStatement {
    
    public JSDefineVarStatement(Statement container, boolean ignoreTypeCheck) {
        super(container, ignoreTypeCheck);
    }

    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public void addVar(BasicObject var, BasicObject val) {
        Main.setLine(row);
        
        if (var.hasFlag("const") && val == LiteralNum.UNDEFINED) {
            throw new CompileException("The constant variable `" + var.getName() + "` must be initialized");
        }
        
        super.addVar(var, val);
    }

    @Override
    public String toTarget(LangBuildTarget builder) {
        List<DefinePair> pairs = getDefinePairs();
        
        boolean first = true;
        StringBuilder str = new StringBuilder();
        for (DefinePair pair: pairs) {
            if (!pair.var.hasLiteral()) {
                if (!first) str.append(",");
                else first = false;
                
                str.append(String.format("%s=%s", pair.var.toTarget(builder), pair.val.toTarget(builder)));
            }
        }
        
        // If first is still set then skipped all inits
        if (first) return "";
        else return "var " + str;
    }
    
}
