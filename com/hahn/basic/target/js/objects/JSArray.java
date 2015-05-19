package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.intermediate.objects.Array;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.target.js.JSPretty;
import com.hahn.basic.util.CompilerUtils;

public class JSArray extends Array {
    
    public JSArray(List<BasicObject> values) {
        super(values);
    }
    
    @Override
    public String toTarget() {
        return JSPretty.format("[%s]", CompilerUtils.toTarget(getValues().toArray(new BasicObject[getValues().size()]), ","));
    }
    
}
