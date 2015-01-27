package com.hahn.basic.intermediate.objects.types;

public class TypeIntLike extends Type {

    public TypeIntLike(String name) {
        super(name);
    }
    
    @Override
    public int getExtendDepth(Type t) {
        if (t == Type.INT) return 0;
        else if (t == Type.CHAR) return 1;
        else if (t == Type.NUMERIC) return 1;
        else return -1;
    }
}
