package com.hahn.basic.intermediate.objects.types;

public class TypeNumeric extends Type {
    
    public TypeNumeric() {
        super("numeric", false, true);
    }
    
    @Override
    public int getExtendDepth(Type t) {
        int intDepth = t.getExtendDepth(Type.INT);
        int floatDepth = t.getExtendDepth(Type.FLOAT);
        
        if (intDepth >= 0 && floatDepth < 0) return intDepth;
        else if (intDepth < 0 && floatDepth >= 0) return floatDepth;
        else if (intDepth >= 0 && floatDepth >= 0) return Math.min(intDepth, floatDepth);
        else return -1;
    }
}
