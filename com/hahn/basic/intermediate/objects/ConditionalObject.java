package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

public abstract class ConditionalObject extends OPObject {
    private BasicObject temp;
    
    public ConditionalObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node, BasicObject temp) {
        super(container, op, p1, p1Node, p2, p2Node);
        
        this.temp = temp;
    }
    
    public Type getType() {
        return Type.BOOL;
    }
    
    public BasicObject getTemp() {
        return temp;
    }
}
