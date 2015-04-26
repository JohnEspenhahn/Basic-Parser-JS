package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.EmptyArray;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class JSEmptyArray extends EmptyArray {

    public JSEmptyArray(Node node, ParameterizedType<Type> type, List<BasicObject> dimensionSizes) {
        super(node, type, dimensionSizes);
    }

    @Override
    public String toTarget() {
        StringBuilder builder = new StringBuilder();
        builder.append(EnumToken.___a);
        builder.append("(");
        
        // If size is defined for at least one dimension
        builder.append("[");
        builder.append(Util.toTarget(getDimensionSizes(), ","));
        builder.append("]");
        
        if (getBaseType().doesExtend(Type.OBJECT)) {
            builder.append(",null");
        } else {
            builder.append(",0");
        }
        
        builder.append(")");
        
        return builder.toString();
    }
    
}
