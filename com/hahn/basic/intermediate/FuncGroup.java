package com.hahn.basic.intermediate;


import java.util.ArrayList;
import java.util.List;

import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangBuildTarget;

public class FuncGroup {
    private List<FuncHead> funcs;
    
    public FuncGroup(FuncHead func) {
        funcs = new ArrayList<FuncHead>();
        
        funcs.add(func);
    }
    
    public void add(FuncHead func) {
        funcs.add(func);
    }
    
    public String getName() {
        return funcs.get(0).getName();
    }
    
    public Type getReturnType() {
        return funcs.get(0).getReturnType();
    }
    
    public boolean isDefined(FuncHead func) {
        for (FuncHead f: funcs) {
            if (f.equals(func)) {
                return true;
            }
        }
        
        return false;
    }
    
    public FuncHead get(ITypeable[] types) {
        for (FuncHead f: funcs) {
            if (f.matches(types)) {
                return f;
            }
        }
        
        return null;
    }
    
    public void toTarget(LangBuildTarget builder) {
        for (FuncHead func: funcs) {
            if (func.hasFrameHead()) {
                func.toTarget(builder);
            }
        }
    }
}
