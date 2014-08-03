package com.hahn.basic.target.js.objects;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public class JSOPObject extends OPObject {
    
    public JSOPObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        if (OPCode.doesModify(getOP())&& getP1().hasFlag("const")) {
            throw new CompileException("Can not modify the constant variable `" + getP1() + "`");
        }
        
        return super.setInUse(by);
    }
    
    public String getPrettyFormat() {
        return (Main.PRETTY_PRINT ? "%s %s %s" : "%s%s%s");
    }
    
    @Override
    public String doToTarget() {
        if (getP2() != null) {
            return String.format(getPrettyFormat(),
                    getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                    getOP().getSymbol(), 
                    getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                   );
        } else {
            return String.format("%s%s", 
                    getOP().getSymbol(),
                    getP1().isExpression() ? "("+getP1().toTarget()+")" : getP1().toTarget()
                   );
        }
    }    
}
