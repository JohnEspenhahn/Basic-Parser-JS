package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.ArithmeticObject;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSArithmeticObject extends ArithmeticObject {
    
    public JSArithmeticObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public String doToTarget() {        
        return JSPretty.format(getFile().isPretty(), "%s_%s_%s",
                getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                getTargetOPSymbol(), 
                getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
               );
    }    
}
