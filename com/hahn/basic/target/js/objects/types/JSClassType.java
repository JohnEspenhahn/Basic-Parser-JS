package com.hahn.basic.target.js.objects.types;

import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.StructType;

public class JSClassType extends ClassType {
    
    public JSClassType(String name, StructType parent) {
        super(name, parent);
    }
    
    @Override
    public String toTarget() {
        return null; // TODO class to target
    }
    
}
