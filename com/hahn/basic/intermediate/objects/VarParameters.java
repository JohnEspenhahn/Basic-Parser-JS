package com.hahn.basic.intermediate.objects;

import lombok.NonNull;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.register.IRegister;
import com.hahn.basic.intermediate.register.StackRegister;

public class VarParameters extends Var {
    private final StackRegister stackReg;
    
    public VarParameters(Frame frame, @NonNull StackRegister stackReg, String name, Type type) {
        super(frame, name, type);
        
        this.stackReg = stackReg;
    }
    
    @Override
    public boolean isRegisterOnStack() {
        return true;
    }
    
    @Override
    protected IRegister getStandardRegister() {
        if (getUses() > 2) {
            return super.getStandardRegister();
        } else {
            return getStackRegister();
        }
    }
    
    @Override
    public StackRegister getStackRegister() {
        return stackReg;
    }
}
