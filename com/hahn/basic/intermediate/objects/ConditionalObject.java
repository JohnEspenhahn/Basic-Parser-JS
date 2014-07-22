package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;

public abstract class ConditionalObject extends BasicObject {
    private OPCode op;
    private BasicObject p1, p2;
    private BasicObject temp;
    
    public ConditionalObject(BasicObject temp, OPCode op, BasicObject p1, BasicObject p2) {
        super(VarTemp.getNextTempName(), Type.BOOL);
        
        this.temp = temp;
        
        this.op = op;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public abstract BasicObject getForUse(Statement s);
    
    public OPCode getOP() {
        return op;
    }
    
    public BasicObject getP1() {
        return p1;
    }
    
    public BasicObject getP2() {
        return p2;
    }
    
    public BasicObject getTemp() {
        return temp;
    }
}
