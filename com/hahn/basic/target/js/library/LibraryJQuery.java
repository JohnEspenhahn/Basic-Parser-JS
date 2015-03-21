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
        ClassType context = Library.defineClass("Context", true);
        
        // Doc
        Library.defineFunc(doc, true, "get", "$", BitFlag.STATIC.b, doc, Type.STRING);
        Library.defineFunc(doc, true, "wrap", "$", BitFlag.STATIC.b, doc, arrDoc);
        
        Library.defineFunc(doc, true, "getHTML", "html", 0, Type.STRING);
        Library.defineFunc(doc, true, "setHTML", "html", 0, Type.VOID, Type.STRING);
        
        Library.defineFunc(doc, true, "append", "append", 0, doc, Type.STRING);
        Library.defineFunc(doc, true, "append", "append", 0, doc, arrDoc);
        Library.defineFunc(doc, true, "append", "append", 0, doc, doc);
        
        Library.defineFunc(doc, true, "getValue", "val", 0, Type.STRING);
        Library.defineFunc(doc, true, "setValue", "val", 0, Type.VOID, Type.STRING);
        
        Library.defineFunc(doc, true, "getContext", "get(0).getContext", 0, context, Type.STRING);
        
        // ArrDoc
        Library.defineParam(arrDoc, "value", "value", Type.STRING);
        Library.defineParam(arrDoc, "html", "innerHTML", Type.STRING);
        Library.defineFunc(arrDoc, true, "getContext", "getContext", 0, context, Type.STRING);

        // DocEvent
        Library.defineParam(docEvent, "target", "target", arrDoc);
        Library.defineParam(docEvent, "mouseX", "pageX", Type.INT);
        Library.defineParam(docEvent, "mouseY", "pageY", Type.INT);
        Library.defineParam(docEvent, "which", "which", Type.CHAR);
        
        // Context
        Library.defineParam(context, "fill", "fillStyle", Type.STRING);
        Library.defineParam(context, "stroke", "strokeStyle", Type.STRING);
        Library.defineFunc(context, true, "beginPath", "beginPath", 0, Type.VOID);
        Library.defineFunc(context, true, "closePath", "closePath", 0, Type.VOID);
        Library.defineFunc(context, true, "moveTo", "moveTo", 0, Type.VOID, new Type[] { Type.INT, Type.INT });
        Library.defineFunc(context, true, "lineTo", "lineTo", 0, Type.VOID, new Type[] { Type.INT, Type.INT });
        Library.defineFunc(context, true, "fill", "fill", 0, Type.VOID);
        Library.defineFunc(context, true, "stroke", "stroke", 0, Type.VOID);
        
        // Doc.onChange
        Library.defineFunc(doc, true, "onChange", "change", 0, Type.VOID, new ParameterizedType<Type>(Type.FUNCTION, new Type[] { docEvent }, Type.VOID));
    }
    
    @Override
    public String toTarget() {
        return "<script src='https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script><script>window.Doc=window</script>";
    }
    
}
