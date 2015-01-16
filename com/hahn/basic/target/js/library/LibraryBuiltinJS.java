package com.hahn.basic.target.js.library;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.BitFlag;

public class LibraryBuiltinJS extends Library {
    
    public LibraryBuiltinJS() {
        super("BuiltinJS");
    }
    
    @Override
    public void define() {
        Type.STRING.systemParam("length", Type.INT, "length", true);
        
        Library.defineFunc("alert", "alert", Type.VOID, Type.OBJECT);
        Library.defineFunc("alert", "alert", Type.VOID, Type.NUMERIC);
        Library.defineFunc("alert", "alert", Type.VOID, Type.STRING);
        
        Library.defineFunc("prompt", "prompt", Type.STRING, Type.STRING);
        
        ClassType console = Library.defineClass("console", true);
        Library.defineFunc(console, "log", "log", BitFlag.STATIC.b, Type.VOID, Type.OBJECT);
        Library.defineFunc(console, "log", "log", BitFlag.STATIC.b, Type.VOID, Type.NUMERIC);
        Library.defineFunc(console, "log", "log", BitFlag.STATIC.b, Type.VOID, Type.STRING);
        
        Library.defineFunc("parseInt", "parseInt", Type.INT, Type.STRING);
        Library.defineFunc("parseFloat", "parseFloat", Type.FLOAT, Type.STRING);
    }
    
    @Override
    public String toTarget() {
        return "";
    }
}
