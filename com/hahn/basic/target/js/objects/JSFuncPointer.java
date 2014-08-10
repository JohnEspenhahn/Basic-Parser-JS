package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.parser.Node;

public class JSFuncPointer extends FuncPointer {
    
    public JSFuncPointer(Node nameNode, BasicObject objectIn, ParameterizedType<ITypeable> funcType) {
        super(nameNode, objectIn, funcType);
    }
    
    @Override
    public String toTarget() {
        if (getObjectIn() == null) return getFuncId();
        else return String.format("%s.%s", getObjectIn().toTarget(), getFuncId());
    }
    
}
