package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.StringConst;

public class JSStringConst extends StringConst {
    
    public JSStringConst(String str) {
        super(str);
    }
    
    @Override
    public String toTarget() {
        return '"' + getString() + '"';
    }
    
}
