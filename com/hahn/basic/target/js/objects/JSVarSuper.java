package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.VarSuper;
import com.hahn.basic.intermediate.objects.types.ClassType;

public class JSVarSuper extends VarSuper {
    
    public JSVarSuper(Frame frame, ClassType type, List<String> flags) {
        super(frame, type, flags);
    }
    
    @Override
    public String toTarget() {
        return getType().getName() + ".prototype";
    }
}
