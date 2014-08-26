package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class NewArray extends BasicObject {
    private BasicObject[] values;
    
    public NewArray(Type containedType, BasicObject[] values) {
        super("new Array<" + containedType + ">", new ParameterizedType<Type>(Type.ARRAY, new Type[] { containedType }));
        
        this.values = values;
    }
    
    public BasicObject[] getValues() {
        return values;
    }
    
    @SuppressWarnings("unchecked")
    public Type getContainedType() {
        return ((ParameterizedType<Type>) getType()).getTypable(0);
    }
}
