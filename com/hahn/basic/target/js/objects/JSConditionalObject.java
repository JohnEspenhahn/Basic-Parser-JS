package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSConditionalObject extends ConditionalObject {
    
    public JSConditionalObject(BasicObject temp, OPCode op, BasicObject p1, BasicObject p2) {
        super(temp, op, p1, p2);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public BasicObject getForUse(Statement s) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
