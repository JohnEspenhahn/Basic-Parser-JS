package com.hahn.basic.intermediate.objects.types;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;

public class TypeList extends Type implements Iterable<Type> {
    private List<Type> types;
    
    public TypeList(@NonNull Type... types) {
        super("[" + StringUtils.join(types, ", ") + "]", false, true);
        
        this.types = Arrays.asList(types);
    }
    
    @Override
    public boolean isDeterminant() {
        return false;
    }
    
    @Override
    public boolean doesExtend(Type other) {
        for (Type t: types) {
            if (!t.doesExtend(other)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public Type castTo(Type newType, int row, int col) {
        for (Type t: this) {
            newType = t.castTo(newType, row, col);
        }
        
        return newType;
    }
    
    @Override
    public Type merge(Type newType, int row, int col, boolean unsafe) {
        for (Type t: this) {
            Type result = t.merge(newType, row, col, unsafe);
            
            if (result == null) return null;
            else newType = result;
        }
        
        return newType;        
    }

    @Override
    public Iterator<Type> iterator() {
        return types.iterator();
    }
}
