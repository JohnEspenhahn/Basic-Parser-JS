package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

import lombok.NonNull;

public abstract class ArithmeticObject extends OPObject {
    
    public ArithmeticObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, @NonNull IBasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        super.setInUse(by);
        
        Type p1Type = getP1().getType();
        Type p2Type = getP2().getType();
        if (p1Type.autocast(getOP().type1, getP1Node().getFile(), getP1Node().getRow(), getP1Node().getCol(), false) == null) {
            throw new CompileException("Expected type `" + getOP().type1 + "` but got `" + p1Type + "` with operator `" + getOP().symbol + "`", getP1Node());
        } else if (p2Type.autocast(getOP().type2, getP1Node().getFile(), getP1Node().getRow(), getP1Node().getCol(), false) == null) {
            throw new CompileException("Expected type `" + getOP().type2 + "` but got `" + p2Type + "` with operator `" + getOP().symbol + "`", getP2Node());
        }
        
        setType(Type.merge(p1Type, p2Type, getP2Node().getFile(), getP2Node().getRow(), getP2Node().getCol(), true));
        
        return false;
    }
}
