package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.DefaultStruct;
import com.hahn.basic.intermediate.objects.types.StructType;

public class JSDefaultStruct extends DefaultStruct {
    
    public JSDefaultStruct(StructType type) {
        super(type);
    }
    
    @Override
    public String toTarget() {
        return "{}";
    }
    
}
