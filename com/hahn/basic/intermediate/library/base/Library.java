package com.hahn.basic.intermediate.library.base;

import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.Util;

public abstract class Library {
    private static final Map<String, Library> libraries = new HashMap<String, Library>();
    
    private final String name;
    protected Library(String name) {
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
    
    public static Library getLib(String name) {
        Library lib = Library.libraries.get(name);

        if (lib != null) {
            return lib;
        } else {
            throw new com.hahn.basic.util.exceptions.CompileException("Unknown library to import " + name);
        }
    }
    
    public static void defineString(String str) {
        // If not already defined will define
        LangCompiler.getString(str);
    }
    
    public static void defineFunc(String name, boolean rawName, Type rtnType) {
        Library.defineFunc(name, rawName, rtnType, new Type[0]);
    }

    public static void defineFunc(String name, boolean rawName, Type rtnType, Type... types) {        
        LangCompiler.defineFunc(name, rawName, rtnType, Util.toParams(types));
    }
}
