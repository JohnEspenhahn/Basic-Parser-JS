package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.VarLocal;
import com.hahn.basic.intermediate.objects.types.Type;

public class JSVarLocal extends VarLocal {
    
    public JSVarLocal(Frame frame, String name, Type type, List<String> flags) {
        super(frame, name, type, flags);
    }
    
}
