package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.ClassType;

public class VarImpliedThis extends VarThis {
    
    public VarImpliedThis(Frame frame, ClassType type) {
        super(frame, type);
    }
    
    @Override
    public int getVarThisFlag() {
        return IS_IMPLIED_THIS;
    }
}
