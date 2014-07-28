package com.hahn.basic.intermediate.objects.types;

public class TypeIntLike extends Type {

    public TypeIntLike(String name) {
        super(name);
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return t == Type.INT || t == Type.CHAR;
    }
}
