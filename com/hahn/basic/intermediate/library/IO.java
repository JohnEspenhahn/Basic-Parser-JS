package com.hahn.basic.intermediate.library;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangBuildTarget;

public class IO extends Library {

    public IO() {
        super("com.hahn.io");
    }

    @Override
    public void define() {
        Library.defineFunc("initIO",      Type.VOID);
        Library.defineFunc("clearScreen", Type.VOID);
        Library.defineFunc("moveCursor",  Type.VOID, new Type[] { Type.UINT, Type.UINT });
        Library.defineFunc("loadCursorX", Type.UINT);
        Library.defineFunc("loadCursorY", Type.UINT);
        Library.defineFunc("scrollUp",    Type.VOID);
        Library.defineFunc("newLine",     Type.VOID);
        Library.defineFunc("print",       Type.VOID, new Type[] { Type.ARRAY });
        Library.defineFunc("print",       Type.VOID, new Type[] { Type.STRING });
        Library.defineFunc("print",       Type.VOID, new Type[] { Type.BOOL });
        Library.defineFunc("print",       Type.VOID, new Type[] { Type.UINT });
        Library.defineFunc("print",       Type.VOID, new Type[] { Type.UINT, Type.BOOL });
        Library.defineFunc("print",       Type.VOID, new Type[] { Type.CHAR });
        Library.defineFunc("println",     Type.VOID, new Type[] { Type.ARRAY });
        Library.defineFunc("println",     Type.VOID, new Type[] { Type.STRING });
        Library.defineFunc("println",     Type.VOID, new Type[] { Type.BOOL });
        Library.defineFunc("println",     Type.VOID, new Type[] { Type.UINT });
        Library.defineFunc("println",     Type.VOID, new Type[] { Type.UINT, Type.BOOL });
        Library.defineFunc("println",     Type.VOID, new Type[] { Type.CHAR });
        
        Library.defineFunc("setKeyCallback"  , Type.VOID, new Type[] { new ParameterizedType<Type>(Type.FUNC, new Type[0]) });
        Library.defineFunc("clearKeyCallback", Type.VOID);
        Library.defineFunc("clearKeyBuffer"  , Type.VOID);
        Library.defineFunc("loadKey"         , Type.UINT);
        Library.defineFunc("isKeyDown"       , Type.BOOL, new Type[] { Type.UINT });
        Library.defineFunc("isKeyDown"       , Type.BOOL, new Type[] { Type.CHAR });
    }
    
    @Override
    public void define(LangBuildTarget b) {
    	// TODO define IO
    }
}
