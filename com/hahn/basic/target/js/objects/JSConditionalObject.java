package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSConditionalObject extends ConditionalObject {
    
    public JSConditionalObject(Statement container, OPCode op, BasicObject p1, BasicObject p2, BasicObject temp) {
        super(container, op, p1, p2, temp);
    }
    
    @Override
    public BasicObject getForUse(Statement s) {
        return this;
    }
    
    @Override
    public String doToTarget(LangBuildTarget builder) {
        return String.format("%s%s%s",
                getP1().isGrouped() ? "("+getP1().toTarget(builder)+")" : getP1().toTarget(builder),
                getOP().getSymbol(), 
                getP2().isGrouped() ? "("+getP2().toTarget(builder)+")" : getP2().toTarget(builder)
               );
    }
    
}
