package com.hahn.basic.target.js.objects;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.VarSuper;
import com.hahn.basic.intermediate.objects.types.ClassType;

public class JSVarSuper extends VarSuper {
    public static final String IDENTIFIER = EnumToken.__s__ + ".prototype";
    
    public JSVarSuper(Frame frame, ClassType type) {
        super(frame, type);
    }
 
    @Override
    public String toTarget() {
        return IDENTIFIER;
    }
}
