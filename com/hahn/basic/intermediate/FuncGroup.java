package com.hahn.basic.intermediate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.Util;

public class FuncGroup implements Iterable<FuncHead> {
    private List<FuncHead> funcs;
    private final String name;
    
    public FuncGroup(FuncHead func) {
        name = func.getName();
        funcs = new ArrayList<FuncHead>();
        
        funcs.add(func);
    }
    
    public void add(FuncHead func) {
        if (!func.getName().equals(getName())) {
            throw new RuntimeException("Function group name mismatch. `" + func.getName() + "` does not match the expected `" + getName() + "`");
        }
        
        funcs.add(func);
    }
    
    public String getName() {
        return name;
    }
    
    public Type getReturnType() {
        return funcs.get(0).getReturnType();
    }
    
    public boolean isDefined(FuncHead func) {
        for (FuncHead f: funcs) {
            if (f.matches(func.getParams())) {
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
    
    public List<FuncHead> getFuncs() {
        return funcs;
    }

    @Override
    public Iterator<FuncHead> iterator() {
        return getFuncs().iterator();
    }

    public boolean isConstructor() {
        return Util.isConstructorName(getName());
    }
}
