package com.hahn.basic.intermediate;

import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.VarGlobal;
import com.hahn.basic.intermediate.objects.VarGlobalStr;
import com.hahn.basic.intermediate.objects.VarTemp;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.opcode.PreprocessorDirective;
import com.hahn.basic.intermediate.register.Register;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.LangFactory;
import com.hahn.basic.target.asm.raw.ASMCommand;
import com.hahn.basic.target.asm.raw.ASMData;
import com.hahn.basic.target.asm.raw.ASMLabel;
import com.hahn.basic.target.asm.raw.ASMSpecial;
import com.hahn.basic.util.CompileException;
import com.hahn.basic.util.Util;

public class Compiler {
    public static final String GLOBAL_PREFIX = "@global_";
    public static final String STRING_PREFIX = "@str_";
    
    public static int NEXT_GLOBAL_IDX;
    public static final int TARGET_BYTECODE_LENGTH = 0x10000;
    
    private static Map<String, Library> libs = new HashMap<String, Library>();
    private static Map<String, Integer> labels = new HashMap<String, Integer>();
    private static Map<String, FuncGroup> funcs = new HashMap<String, FuncGroup>();
    private static Map<String, VarGlobal> globals = new HashMap<String, VarGlobal>();
    
    public static LangFactory factory;
    private static Frame frame;

    public static LangBuildTarget compile(Node h, LangFactory f) {
        frame = new Frame(null, h);
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
        builder.append(new ASMLabel("main"));
        
        frame.toTarget(builder);
        
        // Compile function area
        builder.append(new ASMCommand(OPCode.HNG));
        for (FuncGroup func: funcs.values()) {
            func.toTarget(builder);
        }
        
        // Put library import strings
        for (Library lib: libs.values()) {
            builder.append(lib.getCode());
        }
        
        if (!Main.DEBUG) {
            // Move to constant area
            builder.append(new ASMSpecial(PreprocessorDirective.FILL, "main", 0xC000));
            // Make labels relative to HSG-OG
            builder.append(new ASMSpecial(PreprocessorDirective.ORIGIN, 0x8000));
        }
        
        // Put global vars
        for (VarGlobal var: globals.values()) {
            builder.append(new ASMData(var.getName(), var.getDefaultValue()));
        }
        
        if (!Main.DEBUG) {
            // Fill to VRAM
            builder.append(new ASMSpecial(PreprocessorDirective.FILL, "main", 0xFF80));
        }
        
        return builder;
    }
    
    private static void reset() {
        Compiler.NEXT_GLOBAL_IDX = 0x4000;
        VarTemp.NEXT_TEMP_VAR = 0;
        
        globals.clear();
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
    
    public static VarGlobal getString(String str) {
        String name = STRING_PREFIX + Util.toHexStr(str);
        if (!globals.containsKey(name)) {
            VarGlobal p = new VarGlobalStr(name, str);
            globals.put(name, p);
            
            return p;
        } else {
            return globals.get(name);
        }
    }
    
    public static VarGlobal createGlobalVar(String name, Type type) {
        name = GLOBAL_PREFIX + name;
        if (!globals.containsKey(name)) {
            return new VarGlobal(name, type);
        } else {
            throw new CompileException("The global var `" + name + "` is already defined");
        }
    }
    
    public static void defineGlobalVar(VarGlobal var) {
        String name = var.getName();
        if (!globals.containsKey(name)) {
            globals.put(name, var);
        } else {
            throw new CompileException("The global var `" + name + "` is already defined");
        }
    }
    
    public static VarGlobal getGlobalVar(String name) {
        return globals.get(GLOBAL_PREFIX + name);
    }
    
    public static FuncHead defineFunc(String name, Type rtnType, Param... params) {
        return Compiler.defineFunc(null, name, rtnType, params);
    }
    
    public static FuncHead defineFunc(Node head, String name, Type rtnType, Param... params) {
        FuncHead func = new FuncHead(name, head, rtnType, params);
        
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
}
