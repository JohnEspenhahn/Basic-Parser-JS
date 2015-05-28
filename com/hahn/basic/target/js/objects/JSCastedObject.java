package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.CastedObject;
import com.hahn.basic.intermediate.objects.types.Type;

public class JSCastedObject extends CastedObject {
    
    public JSCastedObject(BasicObject obj, Type type, CodeFile file, int row, int col) {
        super(obj, type, file, row, col);
    }
    
}
