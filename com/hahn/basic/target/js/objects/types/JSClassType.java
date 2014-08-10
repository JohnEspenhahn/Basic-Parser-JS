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
        boolean isChild = (getParent() instanceof ClassType);
        
        StringBuilder builder = new StringBuilder();
        builder.append(JSPretty.format(0, "function %s(){}", getName()));        
        // TODO class default instance values
        // TODO class static values
        
        if (isChild) {            
            builder.append(JSPretty.format(1, "%s.prototype=implements(%s,%s);", getName(), getName(), getParent().getName()));
        }
        
        for (FuncGroup funcGroup: getDefinedFuncs()) {
            for (FuncHead func: funcGroup) {
                builder.append(JSPretty.format(1, "%s.prototype.%s=%s;", getName(), func.getFuncId(), func.toTarget()));
            }
        }
        
        return builder.toString();
    }
    
}
