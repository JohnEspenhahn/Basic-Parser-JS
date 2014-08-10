package com.hahn.basic.intermediate.objects;

import java.util.Arrays;
import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.ClassType;

public class VarThis extends Var {
    private static final List<String> FLAGS = Arrays.asList(new String[] { "const" });
    
    public VarThis(Frame frame, ClassType type) {
        super(frame, "this", type, FLAGS);
    }
    
    @Override
    public boolean isLocal() {
        return false;
    }
}
