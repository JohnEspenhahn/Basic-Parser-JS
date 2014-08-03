package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;

public class JSFuncPointer extends FuncPointer {
    
    public JSFuncPointer(String name, ParameterizedType<ITypeable> funcType) {
        super(name, funcType);
    }
    
    @Override
    public String toTarget() {
        return getFuncId();
    }
    
}
