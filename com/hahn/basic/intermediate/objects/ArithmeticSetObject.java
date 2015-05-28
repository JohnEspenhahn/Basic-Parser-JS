package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.structures.BitFlag;

public abstract class ArithmeticSetObject extends ArithmeticObject {
    
    public ArithmeticSetObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node) {
        super(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        super.setInUse(by);
        
        // Check flags
        if (getP1().hasFlag(BitFlag.CONST)) {
            throw new CompileException("Can not modify the constant variable `" + getP1() + "`", getFile());
        }
        
        // The type of the variable being set
        Type targetType = getP1().getType();
        // The type of the variable to assign
        Type givenType = getP2().getType();
        
        // The type that the target and given want to merge to
        Type mergedType = Type.merge(targetType, givenType, getP2Node().getFile(), getP2Node().getRow(), getP2Node().getCol(), true);
        
        // Ensure the type being merged to can be assigned to the target
        setType(mergedType.autocast(targetType, getP2Node().getFile(), getP2Node().getRow(), getP2Node().getCol(), true));
        
        return false;
    }
    
}
