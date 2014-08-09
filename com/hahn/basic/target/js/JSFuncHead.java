package com.hahn.basic.target.js;

import java.util.Arrays;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public class JSFuncHead extends FuncHead {    
    public JSFuncHead(Frame parent, ClassType classIn, String name, boolean rawName, Node funcHeadNode, Type rtn, Param... params) {
        super(parent, classIn, name, rawName, funcHeadNode, rtn, params);
        
        if (classIn != null) {
            String[] flags = new String[] { "const" };
            LangCompiler.factory.VarLocal(this, "this", classIn, Arrays.asList(flags));
        }
    }
    
    @Override
    public String toFuncAreaTarget() {
        if (getClassIn() != null) return JSPretty.format(0, "function(%l)%f", getParams(), this);
        else return JSPretty.format(0, "function %s(%l)%f", getFuncId(), getParams(), this);        
    }
}
