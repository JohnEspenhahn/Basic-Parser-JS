package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.target.LangBuildTarget;

public class FakeVarUse extends Compilable {
    private AdvancedObject var;
    private boolean isLastUse;
    
    public FakeVarUse(Statement s, AdvancedObject obj) {
        super(s.getFrame());
        
        this.var = obj;
        this.isLastUse = false;
    }

    @Override
    public boolean reverseOptimize() {
        isLastUse = var.setInUse();
        
        return super.reverseOptimize();
    }
    
    @Override
    public boolean forwardOptimize() {
        var.takeRegister(isLastUse);
        
        return super.forwardOptimize();
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) { return ""; }

    @Override
    public String toString() {
        return "use " + var;
    }

}
