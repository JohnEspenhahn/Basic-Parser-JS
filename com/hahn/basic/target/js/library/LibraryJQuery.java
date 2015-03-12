package com.hahn.basic.target.js.library;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.BitFlag;

public class LibraryJQuery extends Library {
    
    public LibraryJQuery() {
        super("Doc");
    }
    
    @Override
    public void define() {        
        ClassType doc = Library.defineClass("Doc", true);
        ClassType arrDoc = Library.defineClass("ArrDoc", true);
        ClassType docEvent = Library.defineClass("DocEvent", true);
        
        Library.defineFunc(doc, true, "get", "$", BitFlag.STATIC.b, doc, Type.STRING);
        Library.defineFunc(doc, true, "wrap", "$", BitFlag.STATIC.b, doc, arrDoc);
        
        Library.defineFunc(doc, true, "getHTML", "html", 0, Type.STRING);
        Library.defineFunc(doc, true, "setHTML", "html", 0, Type.VOID, Type.STRING);
        
        Library.defineFunc(doc, true, "getValue", "val", 0, Type.STRING);
        Library.defineFunc(doc, true, "setValue", "val", 0, Type.VOID, Type.STRING);
        
        Library.defineParam(arrDoc, "value", "value", Type.STRING);
        Library.defineParam(arrDoc, "html", "innerHTML", Type.STRING);
        
        Library.defineParam(docEvent, "target", "target", arrDoc);
        Library.defineParam(docEvent, "mouseX", "pageX", Type.INT);
        Library.defineParam(docEvent, "mouseY", "pageY", Type.INT);
        Library.defineParam(docEvent, "which", "which", Type.CHAR);
        
        Library.defineFunc(doc, true, "onChange", "change", 0, Type.VOID, new ParameterizedType<Type>(Type.FUNCTION, new Type[] { docEvent }, Type.VOID));
    }
    
    @Override
    public String toTarget() {
        return "<script src='https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script><script>window.Doc=window</script>";
    }
    
}
