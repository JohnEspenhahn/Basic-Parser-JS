package com.hahn.basic.intermediate.objects.types;

public class TypeDblLike extends Type {
    
    public TypeDblLike(String name) {
        super(name);
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return t == Type.FLOAT || t == Type.NUMERIC;
    }
}
