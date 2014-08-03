package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;
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
    
    @Override
    public String doToTarget(LangBuildTarget builder) {
        if (getP2() != null) {
            return String.format("%s%s%s",
                    getP1().isGrouped() ? "("+getP1().toTarget(builder)+")" : getP1().toTarget(builder),
                    getOP().getSymbol(), 
                    getP2().isGrouped() ? "("+getP2().toTarget(builder)+")" : getP2().toTarget(builder)
                   );
        } else {
            return String.format("%s%s", 
                    getOP().getSymbol(),
                    getP1().isExpression() ? "("+getP1().toTarget(builder)+")" : getP1().toTarget(builder)
                   );
        }
    }    
}
