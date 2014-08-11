package com.hahn.basic.target.js.statements;

import java.util.List;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;
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
    public void addVar(BasicObject var, BasicObject val, Node node) {
        if (var.hasFlag("const") && val == LiteralNum.UNDEFINED) {
            throw new CompileException("The constant variable `" + var.getName() + "` must be initialized", node);
        }
        
        super.addVar(var, val, node);
    }

    @Override
    public String toTarget() {
        List<DefinePair> pairs = getDefinePairs();
        
        boolean first = true;
        StringBuilder str = new StringBuilder();
        for (DefinePair pair: pairs) {
            if (!pair.var.hasLiteral()) {
                if (!first) str.append(Main.PRETTY_PRINT ? "\n  , " : ",");
                else first = false;
                
                str.append(JSPretty.format("%s_=_%s", pair.var.toTarget(), pair.val.toTarget()));
            }
        }
        
        // If first is still set then skipped all inits
        if (first) return "";
        else return JSPretty.format(0, "var " + str);
    }
    
}
