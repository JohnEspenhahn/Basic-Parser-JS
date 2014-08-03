package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.Util;

public class JSFuncCallPointer extends FuncCallPointer {
    
    public JSFuncCallPointer(String name, BasicObject[] params, int row, int col) {
        super(name, params, row, col);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("%s(%s)", getFuncId(), Util.toTarget(getParams(), ",", builder));
    }
    
}
