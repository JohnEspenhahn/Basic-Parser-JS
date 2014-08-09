package com.hahn.basic.intermediate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.Main;
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
    
    public FuncHead defineFunc(Frame parent, Node head, String name, boolean rawName, Type rtnType, Param... params) {
        FuncHead func = LangCompiler.factory.FuncHead(parent, classType, name, rawName, head, rtnType, params);
        
        FuncGroup group = funcs.get(name);
        if (group == null) {
            group = new FuncGroup(func);
            funcs.put(name, group);
            
            return func;
        } else if (group.isDefined(func)) {
            throw new CompileException("The function `" + func.getName() + "` with those parameters is already defined", Main.getRow(), Main.getCol());
        } else {
            group.add(func);
            
            return func;
        }
    }
    
    public FuncHead getFunc(String name, ITypeable[] types) {
        FuncGroup group = funcs.get(name);
        if (group == null) {
            return null;
        } else {
            return group.get(types);
        }
    }
    
}
