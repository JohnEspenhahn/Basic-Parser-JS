package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.ClassType;

public class VarSuper extends Var {
    
    public VarSuper(Frame frame, ClassType type, List<String> flags) {
        super(frame, "super", type, flags);
    }
    
    @Override
    public boolean isLocal() {
        return false;
    }
}
