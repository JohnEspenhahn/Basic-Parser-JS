package com.hahn.basic.intermediate.statements;


import java.util.ArrayList;
import java.util.List;

import com.hahn.basic.intermediate.objects.AdvancedObject;

public class EndLoopStatement extends Statement {
    /**
     * List of all vars using in this block, but not created within it.
     * Used so last use not tracked as within that block
     */
    private List<AdvancedObject> objs;
    
    public EndLoopStatement(Statement s) {
        super(s);
        
        objs = new ArrayList<AdvancedObject>();
    }
    
    /**
     * Add a var used in a loop but not created within it
     * @param v The var to add
     */
    public void addVar(AdvancedObject o) {
        if (!objs.contains(o)) {
            objs.add(o);
        }
    }
    
    @Override
    public void addTargetCode() {
        for (AdvancedObject o: objs) {
            addCode(new FakeVarUse(this, o));
        }
    }

    @Override
    public String toString() {
        return "endloop";
    }
}
