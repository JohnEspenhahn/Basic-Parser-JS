package com.hahn.basic.util;

import java.util.ArrayList;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.target.LangBuildTarget;

public class IntermediateList<E extends IIntermediate> extends ArrayList<E> {
    private static final long serialVersionUID = 2003797286282703343L;
    
    public String toTarget(LangBuildTarget builder) {
        StringBuilder str = new StringBuilder();
        
        boolean first = true;
        for (IIntermediate i: this) {
            if (!first) str.append(",");
            else first = false;
            
            str.append(i.toTarget(builder));
        }
        
        return str.toString();
    }
}
