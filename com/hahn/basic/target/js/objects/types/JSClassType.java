package com.hahn.basic.target.js.objects.types;

import com.hahn.basic.intermediate.FuncGroup;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.target.js.JSPretty;

public class JSClassType extends ClassType {
    
    public JSClassType(String name, ClassType parent) {
        super(name, parent);
    }
    
    @Override
    public String toTarget() {
        String prefix = "class_";
        String name = prefix + getName();
        
        StringBuilder builder = new StringBuilder();
        builder.append(JSPretty.format(0, "function %s(){};", name));
        // TODO class constructor and default values
        // TODO class static values
        
        if (parent != null) {
            String parentName = prefix + parent.getName();
            
            builder.append(JSPretty.format(0, "%s.prototype=%s.prototype;", name, parentName));
            builder.append(JSPretty.format(0, "%s.prototype.constructor=%s;", name, name));
        }
        
        for (FuncGroup funcGroup: getDefinedFuncs()) {
            for (FuncHead func: funcGroup) {
                builder.append(JSPretty.format(0, "%s.prototype.%s=%s;", name, func.getFuncId(), func.toTarget()));
            }
        }
        
        return builder.toString();
    }
    
}
