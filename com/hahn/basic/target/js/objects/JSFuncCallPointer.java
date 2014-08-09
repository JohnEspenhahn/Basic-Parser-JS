package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class JSFuncCallPointer extends FuncCallPointer {
    
    public JSFuncCallPointer(Node nameNode, BasicObject[] params, int row, int col) {
        super(nameNode, params, row, col);
    }
    
    @Override
    public String toTarget() {
        return String.format("%s(%s)", getFuncId(), Util.toTarget(getParams()));
    }
    
}
