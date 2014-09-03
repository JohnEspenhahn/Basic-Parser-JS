package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.ClassObject;
import com.hahn.basic.intermediate.objects.types.ClassType;

public class JSClassObject extends ClassObject {
    
    public JSClassObject(ClassType type) {
        super(type);
    }
    
    @Override
    public String toTarget() {
        return getName();
    }
    
}
