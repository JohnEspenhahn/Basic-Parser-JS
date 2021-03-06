package com.hahn.basic.intermediate.objects.register;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class IRegister extends BasicObject {
    
    public IRegister(String name) {
        super(name, Type.REAL);
    }

    public abstract boolean isOnStack();
    
    /**
     * Release this register so another object can take it
     */
    public abstract void release();
    
    public abstract IRegister snapshot();
    
    @Override
    public void setName(String name) {
        throw new RuntimeException("Can not set the name of a register");
    }
    
    @Override
    public String toTarget() {
        return toString();
    }
}
