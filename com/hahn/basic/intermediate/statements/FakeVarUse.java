package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.target.LangBuildTarget;

public class FakeVarUse extends Compilable {
    private AdvancedObject var;
    
    public FakeVarUse(Statement s, AdvancedObject obj) {
        super(s.getFrame());
        
        this.var = obj;
    }

    @Override
    public boolean reverseOptimize() {
        var.setInUse(this);
        
        return super.reverseOptimize();
    }
    
    @Override
    public boolean forwardOptimize() {
        var.takeRegister(this);
        
        return super.forwardOptimize();
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) { return ""; }

    @Override
    public String toString() {
        return "use " + var;
    }

}
