package com.hahn.basic.target.js.objects.types;

import com.hahn.basic.intermediate.FuncGroup;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.target.js.JSPretty;

public class JSClassType extends ClassType {
    public static final String PREFIX = "class_";
    
    public JSClassType(String name, ClassType parent) {
        super(name, parent);
    }
    
    @Override
    public String toTarget() {
        String prefixedName = PREFIX + getName();
        
        StringBuilder builder = new StringBuilder();
        builder.append(JSPretty.format(0, "function %s(){};", prefixedName));
        // TODO class default instance values
        // TODO class static values
        
        if (parent != null) {
            String prefixedParent = PREFIX + parent.getName();
            
            builder.append(JSPretty.format(0, "%s.prototype=new %s();", prefixedName, prefixedParent));
            builder.append(JSPretty.format(0, "%s.prototype.constructor=%s;", prefixedName, prefixedName));
        }
        
        for (FuncGroup funcGroup: getDefinedFuncs()) {
            for (FuncHead func: funcGroup) {
                builder.append(JSPretty.format(0, "%s.prototype.%s=%s;", prefixedName, func.getFuncId(), func.toTarget()));
            }
        }
        
        return builder.toString();
    }
    
}
