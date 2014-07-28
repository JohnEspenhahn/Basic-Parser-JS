package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;

public abstract class VarAccess extends BasicObject {
    private BasicObject var;
    private BasicObject index;
    
    /**
     * Access a property of a var at a given index
     * @param var The var to access
     * @param index The index of a property to access. Can be either a literal, another variable, or a struct param
     * @param type The type of the property at the given index
     */
    public VarAccess(BasicObject var, BasicObject index, Type type) {
        super(var.getName() + "[" + index.getName() + "]", type);
        
        this.var = var;
        this.index = index;
    }
    
    public BasicObject getIndex() {
        return index;
    }
    
    public BasicObject getVar() {
        return var;
    }
    
    @Override
    public final BasicObject getForUse(Statement by) {
        getIndex().getForUse(by);
        
        doGetForUse(by);
        
        return super.getForUse(by);
    }
    
    /**
     * Called while still compiling by getForUse, do any
     * finalizations needed in order for this object to be used
     * @param by The calling statement
     */
    public void doGetForUse(Statement by) { }
}
