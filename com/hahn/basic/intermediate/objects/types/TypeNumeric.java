package com.hahn.basic.intermediate.objects.types;

public class TypeNumeric extends Type {
    
    public TypeNumeric() {
        super("numeric", false, true);
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return t.doesExtend(Type.INT) || t.doesExtend(Type.FLOAT);
    }
}
