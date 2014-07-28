package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.target.LangBuildTarget;

public class JSStringConst extends StringConst {
    
    public JSStringConst(String str) {
        super(str);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return '"' + getString() + '"';
    }
    
}
