package com.hahn.basic.intermediate.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.hahn.basic.intermediate.objects.IBasicObject;

public class EndLoopStatement extends Statement {
    /**
     * List of all vars using in this block, but not created within it.
     * Used so last use not tracked as within that block
     */
    private List<IBasicObject> vars;
    
    public EndLoopStatement(Statement s) {
        super(s);
        
        vars = new ArrayList<IBasicObject>();
    }
    
    /**
     * Called while still compiling. Add a var
     * used in a loop but not created within it
     * @param o The object to add
     */
    public void addVar(IBasicObject o) {
        if (!vars.contains(o)) {
            vars.add(o);
        }
    }
    
    public List<IBasicObject> getVars() {
        return vars;
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        ListIterator<IBasicObject> it = vars.listIterator(vars.size());
        while (it.hasPrevious()) {
            IBasicObject obj = it.previous();
            obj.setInUse(this);
        }
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        for (IBasicObject obj: vars) {
            obj.takeRegister(this);
        }
        
        return false;
    }
    
    @Override
    public String toTarget() { 
        return "";
    }

    @Override
    public String toString() {
        return "use " + vars;
    }  
}
