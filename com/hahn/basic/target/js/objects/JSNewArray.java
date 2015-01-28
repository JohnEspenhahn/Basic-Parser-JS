package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.NewArray;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class JSNewArray extends NewArray {
    
    public JSNewArray(Type containedType, Node node, int dimensions, List<BasicObject> values) {
        super(containedType, node, dimensions, values);
    }
    
    @Override
    public String toTarget() {
        StringBuilder builder = new StringBuilder();
        builder.append(EnumToken.__a__);
        builder.append("(");
        builder.append(getDimensions());
        
        // If size is defined for at least one dimension
        if (getDimensionValues().size() > 0) {
            builder.append(",[");
            builder.append(Util.toTarget(getDimensionValues(), ","));
            builder.append("]");
        }
        
        builder.append(")");
        
        return builder.toString();
    }
    
}
