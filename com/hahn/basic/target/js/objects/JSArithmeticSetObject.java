package com.hahn.basic.target.js.objects;

import com.hahn.basic.definition.EnumToken;
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
        // XXX Special condition for updating indexed value of an array
        if (getP1().getAccessedWithinVar() != null && getP1().getAccessedAtIdx() != null 
                && getP1().getAccessedWithinVar().getType().doesExtend(Type.ARRAY)) {
            BasicObject arr = getP1().getAccessedWithinVar();
            BasicObject idx = getP1().getAccessedAtIdx();
            switch (getOP()) {
            case SET:
                return JSPretty.format("%s(%s,%s,%s)",
                        EnumToken.___u,
                        arr.toTarget(),
                        idx.toTarget(),
                        getP2().toTarget()
                       );
            case ADDE:
            case ANDE:
            case BORE:
            case DIVE:
            case MODE:
            case MULE:
            case SUBE:
            case XORE:
                return JSPretty.format("%s(%s,%s,%s(%s,%s)%s%s)",
                        EnumToken.___u,
                        arr.toTarget(),
                        idx.toTarget(),
                        EnumToken.___g,
                        arr.toTarget(),
                        idx.toTarget(),
                        getTargetOPSymbol(),
                        getP2().toTarget()
                       );
            default:
                throw new RuntimeException("Unhandled arithmetic set condition");
            }
        } else {
            return JSPretty.format("%s_%s_%s",
                    getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                    getTargetOPSymbol(), 
                    getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                   );
        }
    }
    
}
