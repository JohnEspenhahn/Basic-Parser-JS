package com.hahn.basic.intermediate.objects.types;

import org.apache.commons.lang3.StringUtils;

public class TypeList extends Type {
    private Type[] types;
    
    public TypeList(Type... types) {
        super("[" + StringUtils.join(types, ", ") + "]", false, true);
        
        this.types = types;
    }
    
    @Override
    public Type castTo(Type newType, int row, int col) {
        for (Type t: types) {
            newType = t.castTo(newType, row, col);
        }
        
        return newType;
    }
    
    @Override
    public Type merge(Type newType, int row, int col, boolean unsafe) {
        for (Type t: types) {
            Type result = t.merge(newType, row, col, unsafe);
            
            if (result == null) return null;
            else newType = result;
        }
        
        return newType;        
    }
}
