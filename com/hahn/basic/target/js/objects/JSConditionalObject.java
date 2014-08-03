package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

public class JSConditionalObject extends ConditionalObject {
    
    public JSConditionalObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node, BasicObject temp) {
        super(container, op, p1, p1Node, p2, p2Node, temp);
    }
    
    @Override
    public String doToTarget() {
        return String.format(getPrettyFormat(),
                getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                getOP().getSymbol(), 
                getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
               );
    }
    
}
