package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;

public class Param extends BasicObject {
    
    public Param(String name, Type type) {
        super(name, type);
    }
    
    @Override
    public String toTarget() {
        throw new RuntimeException("Cannot convert `Param` to target language");
    }
}
