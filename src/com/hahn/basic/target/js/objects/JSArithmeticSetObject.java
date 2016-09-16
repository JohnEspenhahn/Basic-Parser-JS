package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.ArithmeticSetObject;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSArithmeticSetObject extends ArithmeticSetObject {
    
    public JSArithmeticSetObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public String doToTarget() {
        // XXX Special condition for updating indexed value of an array
        if (getP1().getAccessedWithinVar() != null && getP1().getAccessedAtIdx() != null 
                && getP1().getAccessedWithinVar().getType().doesExtend(Type.ARRAY)) {
            IBasicObject arr = getP1().getAccessedWithinVar();
            IBasicObject idx = getP1().getAccessedAtIdx();
            String action = null;
            switch (getOP()) {
            case SET:
                action = "p"; // alias for put function
                break;
            case ADDE:
                action = "a"; // add function
                break;
            case ANDE:
                action = "n"; // and function
                break;
            case BORE:
                action = "b"; // bor function
                break;
            case DIVE:
                action = "d"; // div function
                break;
            case MODE:
                action = "o"; // mod function
                break;
            case MULE:
                action = "m"; // mul function
                break;
            case SUBE:
                action = "s"; // sub function
                break;
            case XORE:
                action = "x"; // xor function
                break;
            default:
                throw new RuntimeException("Unhandled arithmetic set condition");
            }
            
            return JSPretty.format("%s.%s(%s,%s)",
                    arr.toTarget(),
                    action,
                    idx.toTarget(),
                    getP2().toTarget()
                   );
        } else {
            return JSPretty.format("%s_%s_%s",
                    getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                    getTargetOPSymbol(), 
                    getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                   );
        }
    }
    
}
