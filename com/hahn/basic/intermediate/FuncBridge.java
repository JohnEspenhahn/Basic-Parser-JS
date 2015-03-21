package com.hahn.basic.intermediate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public class FuncBridge {
    private final ClassType classType;
    private final Map<String, FuncGroup> funcs;
    
    public FuncBridge(ClassType classType) {
        this.funcs = new HashMap<String, FuncGroup>();
        this.classType = classType;
    }
    
    public Collection<FuncGroup> getFuncs() {
        return funcs.values();
    }
    
    public FuncHead defineFunc(Frame parent, boolean override, Node head, String inName, String outName, Type rtnType, Param... params) {
        FuncHead func = LangCompiler.factory.FuncHead(parent, classType, inName, outName, head, rtnType, params);
        
        FuncGroup group = funcs.get(inName);
        if (group == null) {
            group = new FuncGroup(func);
            funcs.put(inName, group);
            
            return func;
        } else if (!override && group.isDefined(func)) {
            throw new CompileException("The function `" + func.getName() + "` with those parameters is already defined");
        } else {
            if (override) group.removeAllMatch(func);
            
            group.add(func);
            
            return func;
        }
    }
    
    public FuncHead getFunc(String name, ITypeable[] types) {
        FuncGroup group = getFuncGroup(name);
        if (group == null) {
            return null;
        } else {
            return group.get(types, false);
        }
    }
    
    public FuncGroup getFuncGroup(String name) {
        return funcs.get(name);
    }
    
}
