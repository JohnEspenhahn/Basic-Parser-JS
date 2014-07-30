package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.VarParameter;
import com.hahn.basic.intermediate.objects.types.Type;

public class JSVarParameter extends VarParameter {
    
    public JSVarParameter(Frame frame, String name, Type type, List<String> flags) {
        super(frame, name, type, flags);
    }
    
}
