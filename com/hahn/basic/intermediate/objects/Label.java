package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangObject;
import com.hahn.basic.target.asm.raw.ASMInlineLabel;
import com.hahn.basic.util.CompileException;

public class Label extends BasicObject {
    
    public Label(String name) {
        super(name, Type.UINT);
    }
    
    @Override
    public BasicObjectHolder castTo(Type t) {
        throw new CompileException("Can not cast the label '" + getName() + "'");
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof String) {
            return o.equals(getName());
        } else {
            return super.equals(o);
        }
    }

    @Override
    public LangObject toTarget() {
        return new ASMInlineLabel(getName());
    }
}
