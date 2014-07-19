package com.hahn.basic.intermediate.library.base;

import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.library.Common;
import com.hahn.basic.intermediate.library.IO;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.Util;

public abstract class Library {
    private static final Map<String, Library> libraries = new HashMap<String, Library>();
    
    public static final Library Common = new Common(),
                                IO = new IO();
    
    private final String name;
    protected Library(String name) {
        Library.libraries.put(name, this);
        
        this.name = name;
    }
    
    public final ILangCommand getCode() {
        return LangCompiler.factory.Import(name);
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
    public abstract void define(LangBuildTarget b);
    
    public static Library getLib(String name) {
        Library lib = Library.libraries.get(name);

        if (lib != null) {
            return lib;
        } else {
            throw new com.hahn.basic.util.CompileException("Unknown library to import " + name);
        }
    }
    
    public static void defineString(String str) {
        // If not already defined will define
        LangCompiler.getString(str);
    }
    
    public static void defineFunc(String name, Type rtnType) {
        Library.defineFunc(name, rtnType, new Type[0]);
    }

    public static void defineFunc(String name, Type rtnType, Type... types) {        
        LangCompiler.defineFunc(name, rtnType, Util.toParams(types));
    }

    public static void defineProperty(String string) {
        LangCompiler.addGlobalVar(LangCompiler.factory.VarGlobal(string, Type.UINT));
    }
}
