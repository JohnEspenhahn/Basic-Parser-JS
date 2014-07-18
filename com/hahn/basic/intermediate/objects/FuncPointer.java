package com.hahn.basic.intermediate.objects;

import lombok.NonNull;

import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.ILangObject;
import com.hahn.basic.util.CompileException;

public abstract class FuncPointer extends BasicObject {    
    protected FuncHead func;
    
    public FuncPointer(@NonNull String name, @NonNull ParameterizedType<ITypeable> funcType) {
        super(name, funcType);
    }
    
    @Override
    @Deprecated
    public void setType(Type t) {
        throw new RuntimeException("Can not change the type of a FuncPointer!");
    }
    
    public String getFuncId() {
        checkFunction();
        
        return func.getFuncId();
    }
    
    @SuppressWarnings("unchecked")
    private ParameterizedType<ITypeable> getPType() {
        return (ParameterizedType<ITypeable>) super.getType();
    }
    
    public ITypeable[] getTypes() {
        return getPType().getTypes();
    }

    public Type getReturn() {
        return getPType().getReturn();
    }
    
    private void setPTypeReturn(Type t) {
        getPType().setReturn(t);
    }
    
    @Override
    public boolean setInUse() {
        checkFunction();
        
        return super.setInUse();
    }
    
    public void setFunction(FuncHead func) {
        this.func = func;
        
        if (func != null) setPTypeReturn(func.getReturnType());
    }
    
    protected void checkFunction() {
        if (func == null) {
            setFunction(Compiler.getFunc(getName(), getTypes()));
            
            // Still null then not found
            if (func == null) {
                throw new CompileException("Function `" + FuncHead.toHumanReadable(this) + "` was not found");
            }
        }
    }
    
    @Override
    public abstract ILangObject toTarget();
}
