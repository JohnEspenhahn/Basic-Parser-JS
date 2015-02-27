package com.hahn.basic.target.js.library;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.BitFlag;

public class LibraryBuiltinJS extends Library {
    
    public LibraryBuiltinJS() {
        super("BuiltinJS");
    }
    
    @Override
    public void define() {
        Library.defineFunc(Type.OBJECT, true, "toString", "toString", 0, Type.STRING);
        
        Library.defineParam(Type.STRING, "length", "length", Type.INT);
        Library.defineFunc(Type.STRING, true, "matches", "matches", 0, Type.BOOL, Type.STRING);
        Library.defineFunc(Type.STRING, true, "trim", "trim", 0, Type.STRING);
        
        Library.defineFunc(Type.STRING, true, "valueOf", "fromCharCode", BitFlag.STATIC.b, Type.STRING, Type.CHAR);
        Library.defineFunc(Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.INT);
        Library.defineFunc(Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.FLOAT);
        Library.defineFunc(Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.BOOL);
        Library.defineFunc(Type.STRING, true, "valueOf", "fromObject", BitFlag.STATIC.b, Type.STRING, Type.OBJECT);
        
        Library.defineFunc("alert", "alert", Type.VOID, Type.OBJECT);
        Library.defineFunc("alert", "alert", Type.VOID, Type.NUMERIC);
        Library.defineFunc("alert", "alert", Type.VOID, Type.STRING);
        
        Library.defineClass("console", true);
        Library.defineFunc("puts", "console.log", Type.VOID, Type.OBJECT);
        Library.defineFunc("puts", "console.log", Type.VOID, Type.NUMERIC);
        Library.defineFunc("puts", "console.log", Type.VOID, Type.BOOL);
        
        Library.defineFunc("prompt", "prompt", Type.STRING, Type.STRING);
        
        Library.defineFunc("parseInt", "parseInt", Type.INT, Type.STRING);
        Library.defineFunc("parseFloat", "parseFloat", Type.FLOAT, Type.STRING);
    }
    
    @Override
    public String toTarget() {
        return "<script>String.fromObject=function(s){return ''+s};String.prototype.matches=function(s){s=this.match(s);return s!=null&&s[0]==this}</script>";
    }
}
