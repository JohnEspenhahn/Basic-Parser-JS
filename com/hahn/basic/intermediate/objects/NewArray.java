package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class NewArray extends BasicObject {
    private BasicObject[] values;
    private int dimensions;
    
    public NewArray(Type containedType, Node node, int dimensions, BasicObject[] values) {
        super("new Array<" + containedType + ">", new ParameterizedType<Type>(Type.ARRAY, new Type[] { containedType }));
        
        this.values = values;
        this.dimensions = dimensions;
    }
    
    public BasicObject[] getValues() {
        return values;
    }
    
    public int getDimensions() {
        return dimensions;
    }
    
    @SuppressWarnings("unchecked")
    public Type getContainedType() {
        return ((ParameterizedType<Type>) getType()).getTypable(0);
    }
}
