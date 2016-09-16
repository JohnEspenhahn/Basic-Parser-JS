package com.hahn.basic.target.js.library;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.structures.BitFlag;

public class LibraryJS extends Library {
    
    public LibraryJS() {
        super("kava.language.JS");
    }
    
    @Override
    public void define(CodeFile callingFile) {
    	// Define built in Java functions
        Library.defineFunc(callingFile, Type.OBJECT, true, "toString", "toString", 0, Type.STRING);
        
        Library.defineParam(Type.ARRAY, "length", "length", Type.REAL);
        
        Library.defineParam(Type.STRING, "length", "length", Type.REAL);
        Library.defineFunc(callingFile, Type.STRING, true, "matches", "matches", 0, Type.BOOL, Type.STRING);
        Library.defineFunc(callingFile, Type.STRING, true, "trim", "trim", 0, Type.STRING);
        Library.defineFunc(callingFile, Type.STRING, true, "split", "split", 0, ParameterizedType.STRING_ARRAY, Type.STRING);
        
        // TODO Library.defineFunc(Type.STRING, true, "valueOf", "fromCharCode", BitFlag.STATIC.b, Type.STRING, Type.CHAR);
        Library.defineFunc(callingFile, Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.REAL);
        Library.defineFunc(callingFile, Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.BOOL);
        Library.defineFunc(callingFile, Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.OBJECT);
        
        Library.defineFunc(callingFile, "alert", "alert", Type.VOID, Type.OBJECT);
        Library.defineFunc(callingFile, "alert", "alert", Type.VOID, Type.REAL);
        Library.defineFunc(callingFile, "alert", "alert", Type.VOID, Type.STRING);
        Library.defineFunc(callingFile, "alert", "alert", Type.VOID, Type.BOOL);
        
        Library.defineClass(callingFile, "console", true);
        Library.defineFunc(callingFile, "puts", "console.log", Type.VOID, Type.OBJECT);
        Library.defineFunc(callingFile, "puts", "console.log", Type.VOID, Type.REAL);
        Library.defineFunc(callingFile, "puts", "console.log", Type.VOID, Type.BOOL);
        
        Library.defineFunc(callingFile, "prompt", "prompt", Type.STRING, Type.STRING);
        
        Library.defineFunc(callingFile, "parseInt", "parseInt", Type.REAL, Type.STRING);
        Library.defineFunc(callingFile, "parseReal", "parseFloat", Type.REAL, Type.STRING);
    }
    
    @Override
    public String toTarget() {
        return ""; // IOUtil.loadScript(new File("lib/js/js.js.min"));
    }
}
