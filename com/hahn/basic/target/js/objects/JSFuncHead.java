package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.Util;

public class JSFuncHead extends FuncHead {
    
    public JSFuncHead(String name, boolean rawName, Node funcHeadNode, Type rtn, Param... params) {
        super(name, rawName, funcHeadNode, rtn, params);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("function %s(%s){%s}", 
                    getFuncId(), 
                    Util.toString(getParams(), ","),
                    super.toTarget(builder)
                );        
    }
}
