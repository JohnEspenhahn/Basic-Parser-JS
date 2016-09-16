package com.hahn.basic.intermediate.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.ConstructorUtils;
import com.hahn.basic.util.TypeUtils;
import com.hahn.basic.util.exceptions.CompileException;

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
        return get(TypeUtils.getTypes(func.getParams()), true) != null;
    }
    
    public void removeAllMatch(FuncHead func) {
        Iterator<FuncHead> it = funcs.iterator();
        while (it.hasNext()) {
            FuncHead f = it.next();
            if (f.matches(func.getParams())) {
                it.remove();
            }
        }
    }
    
    public FuncHead get(ITypeable[] types, boolean exact) {
        int[] matchDepths = new int[funcs.size()];        
        for (int i = 0; i < matchDepths.length; i++) {
            FuncHead f = funcs.get(i);
            
            int depth = f.getMatchDepth(types);
            if (!exact || depth == 0) matchDepths[i] = depth;
            else matchDepths[i] = -1;
        }
        
        int bestMatch = Integer.MAX_VALUE;
        FuncHead func = null;
        for (int i = 0; i < matchDepths.length; i++) {
            int depth = matchDepths[i];
            if (depth >= 0 && depth < bestMatch) {
                FuncHead tempFunc = funcs.get(i);
                if (bestMatch == depth) {
                    throw new CompileException("Ambiguous definition of `" + tempFunc.toString() + "`", tempFunc.getFile(), tempFunc.row, -1);
                } else {
                    bestMatch = depth;
                    func = tempFunc;
                }
            }
        }
        
        return func;
    }
    
    public List<FuncHead> getFuncs() {
        return funcs;
    }

    @Override
    public Iterator<FuncHead> iterator() {
        return getFuncs().iterator();
    }

    public boolean isConstructor() {
        return ConstructorUtils.isConstructorName(getName());
    }
}
