package com.hahn.basic.intermediate.objects;

import lombok.NonNull;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class ArithmeticObject extends OPObject {
    
    public ArithmeticObject(Statement container, OPCode op, @NonNull BasicObject p1, @NonNull Node p1Node, @NonNull BasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        super.setInUse(by);
        
        if (getP1().getType().autocast(getOP().type1, getP1Node().getRow(), getP1Node().getCol(), false) == null) {
            throw new CompileException("Expected type `" + getOP().type1 + "` but got `" + getP1().getType() + "` with operator `" + getOP().getSymbol() + "`", getP1Node());
        } else if (getP2().getType().autocast(getOP().type2, getP1Node().getRow(), getP1Node().getCol(), false) == null) {
            throw new CompileException("Expected type `" + getOP().type2 + "` but got `" + getP2().getType() + "` with operator `" + getOP().getSymbol() + "`", getP2Node());
        }
        
        setType(Type.merge(getP1().getType(), getP2().getType(), getP2Node().getRow(), getP2Node().getCol(), true));
        
        return false;
    }
}
