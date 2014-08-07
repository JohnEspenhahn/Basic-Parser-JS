package com.hahn.basic.target.js;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public class JSFuncHead extends FuncHead {
    
    public JSFuncHead(Frame parent, String name, boolean rawName, Node funcHeadNode, Type rtn, Param... params) {
        super(parent, name, rawName, funcHeadNode, rtn, params);
    }
    
    @Override
    public String toFuncAreaTarget() {
        return JSPretty.format(0, "function %s(%l)%f", getFuncId(), getParams(), this);        
    }
}
