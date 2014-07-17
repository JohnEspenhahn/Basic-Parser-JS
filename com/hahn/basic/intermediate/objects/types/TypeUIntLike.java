package com.hahn.basic.intermediate.objects.types;

public class TypeUIntLike extends Type {

    public TypeUIntLike(String name) {
        super(name);
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return t == Type.UINT || t == Type.CHAR;
    }
}
