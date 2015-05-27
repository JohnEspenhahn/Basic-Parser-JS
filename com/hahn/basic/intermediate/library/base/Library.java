package com.hahn.basic.intermediate.library.base;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.library.CodeLibrary;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.TypeUtils;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.structures.BitFlag;

public abstract class Library {
    private static final Map<String, Library> libraries = new HashMap<String, Library>();
    
    private final String name;
    protected Library(String name) {
        if (Library.libraries.containsKey(name)) System.out.println("WARNING: THe library '" + name + "' is being overriden!");
        Library.libraries.put(name, this);
        
        this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Library) {
            return ((Library) o).name.equals(this.name);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public abstract void define();
    
    public abstract String toTarget();
    
    public static Library getLib(Node node, String name) {
        Library lib = Library.libraries.get(name);

        if (lib != null) {
            return lib;
        } else {
            try {
                // Everything is done in constructor
                return new CodeLibrary(name);
            } catch (FileNotFoundException e) {
                throw new CompileException("Couldn't load the file '" + name + "'", node);
            }
        }
    }
    
    public static void defineString(String str) {
        // If not already defined will define
        Compiler.getString(str);
    }
    
    public static void defineFunc(String inName, String outName, Type rtnType, Type... types) {        
        Compiler.defineFunc(inName, outName, rtnType, TypeUtils.toParams(types));
    }
    
    public static void defineFunc(ClassType classIn, boolean override, String inName, String outName, int flags, Type rtnType, Type... types) {
        classIn.defineFunc(null, override, inName, outName, rtnType, TypeUtils.toParams(types)).setFlags(flags);
    }
    
    public static void defineParam(ClassType classIn, String inName, String outName, Type type) {
        classIn.systemParam(inName, type, outName, true);
    }
    
    public static ClassType defineClass(String name, boolean isFinal) {
        return Type.OBJECT.extendAs(null, name, BitFlag.SYSTEM.b | (isFinal ? BitFlag.FINAL.b : 0));
    }
}
