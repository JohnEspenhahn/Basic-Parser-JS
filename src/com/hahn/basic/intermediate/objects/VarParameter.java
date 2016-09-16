package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;

public class VarParameter extends Var {    
	
    public VarParameter(Frame frame, String name, Type type, int flags) {
        super(frame, name, type, flags);
    }
    
    @Override
    public boolean isLocal() {
        return true;
    }
}
