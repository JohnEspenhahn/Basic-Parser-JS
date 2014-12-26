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
        Library.defineFunc("alert", true, Type.VOID, Type.OBJECT);
        Library.defineFunc("alert", true, Type.VOID, Type.NUMERIC);
        Library.defineFunc("alert", true, Type.VOID, Type.STRING);
        
        ClassType console = Library.defineClass("console", true);
        Library.defineFunc(console, "log", true, BitFlag.STATIC.b, Type.VOID, Type.OBJECT);
        Library.defineFunc(console, "log", true, BitFlag.STATIC.b, Type.VOID, Type.NUMERIC);
        Library.defineFunc(console, "log", true, BitFlag.STATIC.b, Type.VOID, Type.STRING);
    }
    
    @Override
    public String toTarget() {
        return "";
    }
}
