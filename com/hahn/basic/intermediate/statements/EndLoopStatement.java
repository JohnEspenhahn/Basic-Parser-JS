package com.hahn.basic.intermediate.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.hahn.basic.intermediate.objects.BasicObject;

public class EndLoopStatement extends Statement {
    /**
     * List of all vars using in this block, but not created within it.
     * Used so last use not tracked as within that block
     */
    private List<BasicObject> objs;
    
    public EndLoopStatement(Statement s) {
        super(s);
        
        objs = new ArrayList<BasicObject>();
    }
    
    /**
     * Called while still compiling. Add a var
     * used in a loop but not created within it
     * @param o The object to add
     */
    public void addVar(BasicObject o) {
        if (!objs.contains(o)) {
            objs.add(o);
        }
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        ListIterator<BasicObject> it = objs.listIterator(objs.size());
        while (it.hasPrevious()) {
            BasicObject obj = it.previous();
            obj.setInUse(this);
        }
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        for (BasicObject obj: objs) {
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
        return "use " + objs;
    }  
}
