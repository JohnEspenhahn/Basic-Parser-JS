package com.hahn.basic.target.js.library;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.BitFlag;

public class LibraryJQuery extends Library {
    
    public LibraryJQuery() {
        super("dom");
    }
    
    @Override
    public void define() {
        ClassType dom = Library.defineClass("dom", true);
        Library.defineFunc(dom, true, "get", "$", BitFlag.STATIC.b, dom, Type.STRING);
        Library.defineFunc(dom, true, "getHTML", "html", 0, Type.STRING);
        Library.defineFunc(dom, true, "setHTML", "html", 0, Type.VOID, Type.STRING);
        
        Library.defineFunc(dom, true, "getValue", "val", 0, Type.STRING);
        Library.defineFunc(dom, true, "setValue", "val", 0, Type.VOID, Type.STRING);
        
        Library.defineFunc(dom, true, "onChange", "change", 0, Type.VOID, new ParameterizedType<Type>(Type.FUNCTION, new Type[0], Type.VOID));
    }
    
    @Override
    public String toTarget() {
        return "<script src='https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script><script>window.dom=window</script>";
    }
    
}
