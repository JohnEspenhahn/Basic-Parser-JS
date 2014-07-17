package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangObject;
import com.hahn.basic.target.asm.raw.ASMInlineLabel;
import com.hahn.basic.util.Util;

public class VarGlobalStr extends VarGlobal {

    public VarGlobalStr(String name, String value) {
        super(name, Type.STRING, Util.chars2intarr(value));
    }
    
    @Override
    public BasicObject getAddress() {
        return new Label(getName());
    }

    @Override
    public AdvancedObject getPointer() {
        return this;
    }
    
    @Override
    public LangObject toTarget() {
        return new ASMInlineLabel(getName());
    }
}
