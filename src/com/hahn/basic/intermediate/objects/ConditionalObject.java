package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

import lombok.NonNull;

public abstract class ConditionalObject extends OPObject {
    private IBasicObject temp;
    
    /**
     * Create a conditional object
     * @param container The container of this object
     * @param op The operation to preform
     * @param p1 The nonull first parameter
     * @param p1Node Used when throwing errors related to p1
     * @param p2 The nonull second parameter
     * @param p2Node Used when throwing errors related to p2
     * @param temp The temporary object that can be used when compiling this
     */
    public ConditionalObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, @NonNull IBasicObject p2, Node p2Node, IBasicObject temp) {
        super(container, op, p1, p1Node, p2, p2Node);
        
        this.temp = temp;
    }
    
    @Override
    public Type getType() {
        return Type.BOOL;
    }
    
    /**
     * Get the provided temporary object
     * @return The provided temporary object
     */
    public IBasicObject getTemp() {
        return temp;
    }
}
