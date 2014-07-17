package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangObject;

public class Param extends BasicObject {
    
    public Param(String name, Type type) {
        super(name, type);
    }
    
    @Override
    public LangObject toTarget() {
        throw new RuntimeException("Cannot add 'Param' as ASM");
    }
}
