package com.hahn.basic.intermediate.objects.types;

public class RealOrStringType extends Type {
    
    public RealOrStringType() {
        super(null, "real_string", false, true);
    }
    
    @Override
    public int getExtendDepth(Type t) {
        int realDepth = t.getExtendDepth(Type.REAL);
        int stringDepth = t.getExtendDepth(Type.STRING);
        
        if (realDepth >= 0 && stringDepth >= 0) return Math.min(realDepth, stringDepth);
        else if (stringDepth < 0) return realDepth;
        else if (realDepth < 0) return stringDepth;
        else return -1;

    }
}
