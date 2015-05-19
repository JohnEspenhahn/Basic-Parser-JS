package com.hahn.basic.target.js.library;

import java.io.File;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.IOUtil;
import com.hahn.basic.util.structures.BitFlag;

public class LibraryJS extends Library {
    
    public LibraryJS() {
        super("JS");
    }
    
    @Override
    public void define() {
        Library.defineFunc(Type.OBJECT, true, "toString", "toString", 0, Type.STRING);
        
        Library.defineParam(Type.ARRAY, "length", "length", Type.REAL);
        
        Library.defineParam(Type.STRING, "length", "length", Type.REAL);
        Library.defineFunc(Type.STRING, true, "matches", "matches", 0, Type.BOOL, Type.STRING);
        Library.defineFunc(Type.STRING, true, "trim", "trim", 0, Type.STRING);
        Library.defineFunc(Type.STRING, true, "split", "split", 0, ParameterizedType.STRING_ARRAY, Type.STRING);
        
        // TODO Library.defineFunc(Type.STRING, true, "valueOf", "fromCharCode", BitFlag.STATIC.b, Type.STRING, Type.CHAR);
        Library.defineFunc(Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.REAL);
        Library.defineFunc(Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.BOOL);
        Library.defineFunc(Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.OBJECT);
        
        Library.defineFunc("alert", "alert", Type.VOID, Type.OBJECT);
        Library.defineFunc("alert", "alert", Type.VOID, Type.REAL);
        Library.defineFunc("alert", "alert", Type.VOID, Type.STRING);
        Library.defineFunc("alert", "alert", Type.VOID, Type.BOOL);
        
        Library.defineClass("console", true);
        Library.defineFunc("puts", "console.log", Type.VOID, Type.OBJECT);
        Library.defineFunc("puts", "console.log", Type.VOID, Type.REAL);
        Library.defineFunc("puts", "console.log", Type.VOID, Type.BOOL);
        
        Library.defineFunc("prompt", "prompt", Type.STRING, Type.STRING);
        
        Library.defineFunc("parseInt", "parseInt", Type.REAL, Type.STRING);
        Library.defineFunc("parseReal", "parseFloat", Type.REAL, Type.STRING);
    }
    
    @Override
    public String toTarget() {
        return IOUtil.loadScript(new File("lib/js/js.js.min"));
    }
}
