package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSOPObject extends OPObject {
    
    public JSOPObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public String doToTarget() {
        if (getP2() != null) {
            return JSPretty.format("%s_%s_%s",
                    getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                    getOP().getSymbol(), 
                    getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                   );
        } else {
            return JSPretty.format("%s%s",
                    getOP().getSymbol(),
                    getP1().isExpression() ? "("+getP1().toTarget()+")" : getP1().toTarget()
                   );
        }
    }    
}
