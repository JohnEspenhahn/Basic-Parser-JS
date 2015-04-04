package com.hahn.basic.target.js.objects;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.ArithmeticObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSArithmeticObject extends ArithmeticObject {
    
    public JSArithmeticObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public boolean isGrouped() {
        return super.isGrouped() ||
                (getType().doesExtend(Type.INT) && getOP() == OPCode.DIV || getOP() == OPCode.MOD);
    }
    
    @Override
    public String doToTarget() {
        // Special conditions for javascript integer division
        boolean isInt = getType().doesExtend(Type.INT);
        if (isInt) {
            switch (getOP()) {
            case DIV:
                return JSPretty.format("%s(%s,%s)|0",
                        EnumToken.___d,
                        getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                        getOP().getSymbol(), 
                        getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                       );
            case MOD:
                return JSPretty.format("%s(%s,%s)|0",
                        EnumToken.___m,
                        getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                        getOP().getSymbol(), 
                        getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                       );
            default:
            }
        } else {
            switch (getOP()) {
            case DIV:
                return JSPretty.format("%s(%s,%s)",
                        EnumToken.___d,
                        getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                        getOP().getSymbol(), 
                        getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                       );
            case MOD:
                return JSPretty.format("%s(%s,%s)",
                        EnumToken.___m,
                        getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                        getOP().getSymbol(), 
                        getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                       );
            default:
            }
        }
        
        // Default format
        return JSPretty.format("%s_%s_%s",
                getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                getOP().getSymbol(), 
                getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
               );
    }    
}
