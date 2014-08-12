package com.hahn.basic.intermediate.objects.types;

public class TypeNumber extends Type {
    
    public TypeNumber() {
        super("num", false, true);
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return t.doesExtend(Type.INT) || t.doesExtend(Type.DBL);
    }
}
