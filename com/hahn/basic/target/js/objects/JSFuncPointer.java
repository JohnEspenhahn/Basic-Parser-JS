package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.parser.Node;

public class JSFuncPointer extends FuncPointer {
    
    public JSFuncPointer(Node nameNode, ParameterizedType<ITypeable> funcType) {
        super(nameNode, funcType);
    }
    
    @Override
    public String toTarget() {
        return getFuncId();
    }
    
}
