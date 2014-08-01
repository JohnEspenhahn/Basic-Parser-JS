package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class VarAccess extends BasicObjectHolder {
    private BasicObject index;
    
    /**
     * Access a property of a var at a given index
     * @param var The var to access
     * @param index The index of a property to access. Can be either a literal, another variable, or a struct param
     * @param type The type of the property at the given index
     */
    public VarAccess(Statement container, BasicObject var, BasicObject index, Type type) {
        super(var, type);
        
        if (!var.isVar()) {
            throw new CompileException("Illegal attempt to access object `" + var + "` at index `" + index + "`");
        }
        
        this.index = index;
    }
    
    public BasicObject getIndex() {
        return index;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        getIndex().setInUse(by);
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        getIndex().takeRegister(by);
        
        super.takeRegister(by);
    }
    
    @Override
    public abstract String toTarget(LangBuildTarget builder);
}
