package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.parser.Node;

public abstract class ParamDefaultValStatement extends DefineVarStatement {
    
    public ParamDefaultValStatement(FuncHead func, boolean ignoreTypeCheck) {
        super(func, ignoreTypeCheck);
    }
    
    /**
     * Add a variable to be defined in this statement
     * @param pair The pair with the simple parameter as the var. This will
     * be converted to the actual function parameter variable
     */
    public void addVar(DefinePair pair) {
        IBasicObject actualVar = getFrame().safeGetVar(pair.var.getName());
        if (actualVar.getType() != pair.var.getType()) {
            throw new RuntimeException("Unexpected type `" + actualVar.getType() + "` of actual var. Expected `" + pair.var.getType() + "`");
        }
        
        ((FuncHead) getFrame()).makeOptional(actualVar);
        
        super.addVar(actualVar, pair.val, pair.node);
    }
    
    @Deprecated
    public void addVar(IBasicObject var, IBasicObject val, Node node) {
        throw new RuntimeException("Please use addVar(DefinePair) with ParamDefaultVarStatement");
    }
    
    @Override
    public boolean reverseOptimize() {
        // Invalidate literals
        for (DefinePair pair: getDefinePairs()) {
            pair.var.setLiteral(null);
        }
        
        return super.reverseOptimize();
    }
}
