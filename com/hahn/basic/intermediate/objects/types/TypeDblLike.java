package com.hahn.basic.intermediate.objects.types;

public class TypeDblLike extends Type {
    
    public TypeDblLike(String name) {
        super(name);
    }
    
    @Override
    public int getExtendDepth(Type t) {
        if (t == Type.FLOAT) return 0;
        else if (t == Type.NUMERIC) return 1;
        else return -1;
    }
}
