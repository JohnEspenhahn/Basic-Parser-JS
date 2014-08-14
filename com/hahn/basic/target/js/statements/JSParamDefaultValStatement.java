package com.hahn.basic.target.js.statements;

import java.util.Iterator;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.statements.ParamDefaultValStatement;
import com.hahn.basic.target.js.JSPretty;

public class JSParamDefaultValStatement extends ParamDefaultValStatement {
    
    public JSParamDefaultValStatement(FuncHead func, boolean ignoreTypeCheck) {
        super(func, ignoreTypeCheck);
    }
    
    @Override
    public String toTarget() {        
        StringBuilder str = new StringBuilder();
        
        Iterator<DefinePair> it = getDefinePairs().iterator();
        while (it.hasNext()) {
            DefinePair pair = it.next();
            str.append(
                    JSPretty.format(0, "%s_=_typeof %s_!==_'undefined'_?_%s" + (it.hasNext() ? ";^" : ""), 
                      pair.var.toTarget(),
                      pair.var.toTarget(),
                      pair.val.isGrouped() ? "("+pair.val.toTarget()+")" : pair.val.toTarget()
                    )
                );
        }
        
        return str.toString();
    }
    
}
