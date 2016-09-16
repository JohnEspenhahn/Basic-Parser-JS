package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.intermediate.objects.Array;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.target.js.JSPretty;
import com.hahn.basic.util.CompilerUtils;

public class JSArray extends Array {
    
    public JSArray(List<IBasicObject> values) {
        super(values);
    }
    
    @Override
    public String toTarget() {
        return JSPretty.format("[%s]", CompilerUtils.toTarget(getValues().toArray(new IBasicObject[getValues().size()]), ","));
    }
    
}
