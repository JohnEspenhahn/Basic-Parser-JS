package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.NewArray;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class JSNewArray extends NewArray {
    
    public JSNewArray(Type containedType, Node node, int dimensions, BasicObject[] values) {
        super(containedType, node, dimensions, values);
    }
    
    @Override
    public String toTarget() {
        return "[" + Util.toTarget(getValues(), ",") + "]";
    }
    
}
