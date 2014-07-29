package com.hahn.basic.target.js.library;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangBuildTarget;

public class LibraryBuiltinJS extends Library {
    
    public LibraryBuiltinJS() {
        super("BuiltinJS");
    }
    
    @Override
    public void define() {
        Library.defineFunc("alert", true, Type.VOID, Type.STRING);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return "";
    }
}
