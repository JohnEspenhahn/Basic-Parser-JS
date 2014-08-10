package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class JSFuncCallPointer extends FuncCallPointer {
    
    public JSFuncCallPointer(Node nameNode, BasicObject objectIn, BasicObject[] params) {
        super(nameNode, objectIn, params);
    }
    
    @Override
    public String toTarget() {
        if (getObjectIn() == null) return String.format("%s(%s)", getFuncId(), Util.toTarget(getParams()));
        else return String.format("%s.%s(%s)", getObjectIn().toTarget(), getFuncId(), Util.toTarget(getParams()));
    }    
}
