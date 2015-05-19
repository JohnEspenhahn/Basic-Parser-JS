package com.hahn.basic.target.js.function;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSFuncHead extends FuncHead {    
    public JSFuncHead(Frame parent, ClassType classIn, String inName, String outName, Node funcHeadNode, Type rtn, Param... params) {
        super(parent, classIn, inName, outName, funcHeadNode, rtn, params);
    }
    
    @Override
    public String toFuncAreaTarget() {
        if (getClassIn() != null) return JSPretty.format("function(%l)%f", getParams(), this);
        else return JSPretty.format(0, "function %s(%l)%f", getFuncId(), getParams(), this);
    }
}
