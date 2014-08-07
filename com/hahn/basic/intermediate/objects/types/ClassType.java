package com.hahn.basic.intermediate.objects.types;

import java.util.List;

import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.BasicObject;

public abstract class ClassType extends StructType {
    
    public ClassType(String name, StructType parent) {
        super(name, parent);
    }
    
    /**
     * Extend this
     * @param name The name of the new class
     * @param ps The parameters added by this new class
     * @return A new class object
     */
    @Override
    public ClassType extendAs(String name, List<BasicObject> ps) {
        ClassType newClass = LangCompiler.factory.ClassType(name, this);
        newClass.loadVars(ps);
        
        return newClass;
    }
    
    public abstract String toTarget();
}
