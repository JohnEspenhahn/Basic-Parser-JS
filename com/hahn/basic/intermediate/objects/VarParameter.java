package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class VarParameter extends Var {    
	
    public VarParameter(Frame frame, String name, Type type) {
        super(frame, name, type);
    }
    
}
