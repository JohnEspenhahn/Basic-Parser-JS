package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.ClassType;

public class VarThis extends Var {
    
    public VarThis(Frame frame, ClassType type, List<String> flags) {
        super(frame, "this", type, flags);
    }
    
    @Override
    public boolean isLocal() {
        return false;
    }
}
