package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;

public interface IArray {
    
    /**
     * Get the base type of this array. Not guaranteed to be accurate until after reverseOptimize
     * @return The base type of the array (ex: `int` for `new int[3][]`)
     */
    Type getBaseType();
    
    /**
     * Get the dimensions of this array. Not guaranteed to be accurate until after reverseOptimize
     * @return The dimensions of hte array (ex: `2` for `new int[3][]`)
     */
    int dimensions();
    
    static ParameterizedType<Type> toArrayType(Type baseType, int dimensions) {
        Type[] parameterizedTypesArr = new Type[dimensions];
        for (int i = 0; i < parameterizedTypesArr.length; i++) {
            int iDim = dimensions - i - 1;
            if (iDim > 0) parameterizedTypesArr[i] = toArrayType(baseType, iDim);
            else parameterizedTypesArr[i] = baseType;
        }
        
        return new ParameterizedType<Type>(Type.ARRAY, parameterizedTypesArr);
    }
    
    static Type getBaseType(ParameterizedType<ITypeable> type) {
        return type.getTypable(type.numTypeParams() - 1).getType();
    }
    
}
