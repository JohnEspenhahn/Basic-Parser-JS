package com.hahn.basic.intermediate;

import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.VarGlobal;
import com.hahn.basic.intermediate.objects.VarTemp;
import com.hahn.basic.intermediate.objects.register.Register;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.ILangFactory;
import com.hahn.basic.util.exceptions.CompileException;

public class LangCompiler {    
    private static Map<String, Library> libs = new HashMap<String, Library>();
    private static Map<String, Integer> labels = new HashMap<String, Integer>();
    private static Map<String, FuncGroup> funcs = new HashMap<String, FuncGroup>();
    private static Map<String, StringConst> strings = new HashMap<String, StringConst>();
    
    public static ILangFactory factory;
    private static Frame globalFrame, frame;

    public static LangBuildTarget compile(Node h, ILangFactory f) {
        globalFrame = frame = new Frame(null, h);
        factory = f;
        
        // Reset
        Register.freeAll();
        // Type.reset();
        reset();
        
        // Optimize
        frame.reverseOptimize();
        frame.forwardOptimize();
        
        // Convert to target
        LangBuildTarget builder = factory.LangBuildTarget();        
        frame.toTarget(builder);
        builder.endCodeArea();
        
        // Compile function area
        for (FuncGroup func: funcs.values()) {
            func.toTarget(builder);
        }
        
        // Put library import strings
        for (Library lib: libs.values()) {
            builder.append(lib.getCode());
        }
        
        return builder;
    }
    
    private static void reset() {
        VarTemp.NEXT_TEMP_VAR = 0;
        
        strings.clear();
        labels.clear();
        funcs.clear();
        libs.clear();
    }
    
    public static String getLabel(String name, Frame f) {
        if (f instanceof FuncHead) {
            name = ((FuncHead) f).getFuncId().toString() + name; 
        }
        
        Integer idx = labels.get(name);
        if (idx == null) {
            labels.put(name, 1);
            return name + "_0";
        } else {
            labels.put(name, idx + 1);
            return name + "_" + idx;
        }
    }
    
    public static void addLibrary(String name) {        
        if (!libs.containsKey(name)) {
            Library lib = Library.getLib(name); 
            libs.put(name, lib);
            
            lib.define();
        }
    }
    
    public static StringConst getString(String str) {
    	StringConst var = strings.get(str);
    	if (var != null) {
    		return var;
    	} else {
    		StringConst strConst = LangCompiler.factory.StringConst(str);
    		strings.put(str, strConst);
    		
    		return strConst;
    	}
    }
    
    public static VarGlobal addGlobalVar(VarGlobal var) {
        return (VarGlobal) globalFrame.addVar(var);
    }
    
    public static FuncHead defineFunc(String name, Type rtnType, Param... params) {
        return LangCompiler.defineFunc(null, name, rtnType, params);
    }
    
    public static FuncHead defineFunc(Node head, String name, Type rtnType, Param... params) {
        FuncHead func = LangCompiler.factory.FuncHead(name, head, rtnType, params);
        
        FuncGroup old = funcs.get(name);
        if (old == null) {
            FuncGroup group = new FuncGroup(func);
            funcs.put(name, group);
            
            return func;
        } else if (old.isDefined(func)) {
            throw new CompileException("The function `" + func.getName() + "` with those parameters is already defined");
        } else {
            FuncGroup group = funcs.get(name);
            group.add(func);
            
            return func;
        }
    }
    
    public static FuncHead getFunc(String name, ITypeable[] types) {
        FuncGroup group = funcs.get(name);
        if (group == null) {
            return null;
        } else {
            return group.get(types);
        }
    }
    
    public static Frame getGlobalFrame() {
    	return globalFrame;
    }
}
