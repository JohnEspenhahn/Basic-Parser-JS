package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

public class PostfixOPObject extends OPObject {
    
    public PostfixOPObject(Statement container, OPCode op, BasicObject p, Node pNode) {
        super(container, op, p, pNode, null, null);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        super.setInUse(by);
     
        setType(getParam().getType());
        
        return false;
    }
    
    public BasicObject getParam() {
        return getP1();
    }
    
    @Override
    public String toString() {
        return String.format("%s%s", getParam().getName(), getOP().getSymbol());
    }

    @Override
    public String doToTarget() {
        return String.format("%s%s", getParam().toTarget(), getOP().getSymbol());
    }
}
