package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class ArithmeticSetObject extends ArithmeticObject {
    
    public ArithmeticSetObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        super.setInUse(by);
        
        // Check flags
        if (getP1().hasFlag(BitFlag.CONST)) {
            throw new CompileException("Can not modify the constant variable `" + getP1() + "`");
        }
        
        setType(getP2().getType().autocast(getP1().getType(), getP2Node().getRow(), getP2Node().getCol(), true));
        
        return false;
    }
    
}
