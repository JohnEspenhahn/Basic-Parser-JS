package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.ClassType;

public abstract class ClassObject extends BasicObject {
    
    public ClassObject(ClassType type) {
        super(type.getName(), new StaticClassTypeHolder(type));
    }    
    
    @Override
    public boolean isClassObject() {
        return true;
    }
}
