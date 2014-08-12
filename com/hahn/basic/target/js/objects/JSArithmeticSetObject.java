package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.ArithmeticSetObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSArithmeticSetObject extends ArithmeticSetObject {
    
    public JSArithmeticSetObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public String doToTarget() {
        // Special conditions for javascript integer division
        if (getType().doesExtend(Type.INT) && getOP() == OPCode.DIVE || getOP() == OPCode.MODE) {
            return JSPretty.format("%s=%s_%s_%s|0",
                    getP1().toTarget(),
                    getP1().toTarget(),
                    (getOP() == OPCode.DIVE ? OPCode.DIV : OPCode.MOD).getSymbol(),
                    getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                   );
        }
        
     // Default format
        return JSPretty.format("%s_%s_%s",
                getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                getOP().getSymbol(), 
                getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
               );
    }
    
}
