package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;

public abstract class ConditionalObject extends OPObject {
    private BasicObject temp;
    
    public ConditionalObject(Statement container, OPCode opcode, BasicObject p1, BasicObject p2, BasicObject temp) {
        super(container, opcode, p1, p2);
        
        this.temp = temp;
    }
    
    public Type getType() {
        return Type.BOOL;
    }
    
    public BasicObject getTemp() {
        return temp;
    }
}
