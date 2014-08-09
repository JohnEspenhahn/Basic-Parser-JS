package com.hahn.basic.intermediate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;

public class FuncGroup implements Iterable<FuncHead> {
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
    
    public List<FuncHead> getFuncs() {
        return funcs;
    }

    @Override
    public Iterator<FuncHead> iterator() {
        return getFuncs().iterator();
    }
}
