package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.StructType;

public abstract class DefaultStruct extends BasicObject {
    
    public DefaultStruct(StructType type) {
        super("new Struct()", type);
    }
    
}
