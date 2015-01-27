package com.hahn.basic.intermediate.objects;

import java.util.List;

import lombok.NonNull;

import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public abstract class NewArray extends BasicObject {
    private List<BasicObject> dimensionValues;
    private int dimensions;
    
    public NewArray(Type containedType, Node node, int dimensions, @NonNull List<BasicObject> dimValues) {
        super("new Array<" + containedType + ">", new ParameterizedType<Type>(Type.ARRAY, Util.createArray(dimensions, containedType)));
        
        this.dimensionValues = dimValues;        
        this.dimensions = dimensions;
    }
    
    public List<BasicObject> getDimensionValues() {
        return dimensionValues;
    }
    
    public int getDimensions() {
        return dimensions;
    }
    
    @SuppressWarnings("unchecked")
    public Type getContainedType() {
        return ((ParameterizedType<Type>) getType()).getTypable(0);
    }
}
