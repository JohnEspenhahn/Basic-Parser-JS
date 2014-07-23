package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public class JSFuncHead extends FuncHead {
    
    public JSFuncHead(String name, Node funcHeadNode, Type rtn, Param... params) {
        super(name, funcHeadNode, rtn, params);
    }
    
    @Override
    protected void addPreTargetCode() {
        
    }
    
}
