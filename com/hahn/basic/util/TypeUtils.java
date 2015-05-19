package com.hahn.basic.util;

import java.util.Arrays;
import java.util.List;

import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;

public class TypeUtils {
    
    public static Type[] getTypes(ITypeable[] params) {
        Type[] arr = new Type[params.length];
        for (int i = 0; i < params.length; i++) {
            arr[i] = params[i].getType();
        }
        
        return arr;
    }
    
    public static Param[] toParams(AdvancedObject... params) {
        Param[] arr = new Param[params.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new Param(params[i].getName(), params[i].getType());
        }
        
        return arr;
    }
    
    public static Param[] toParams(Type... types) {
        return toParams(Arrays.asList(types));
    }
    
    public static Param[] toParams(List<Type> types) {
        Param[] arr = new Param[types.size()];
        for (int i = 0; i < arr.length; i++) {
            Type t = types.get(i);
            arr[i] = new Param("@param_" + i, t);
        }
        
        return arr;
    }
    
    public static String joinTypes(ITypeable[] arr, char seperator) {
        StringBuffer result = new StringBuffer();
        
        boolean first = true;
        for (int i = 0; i < arr.length; i++) {
           if (first) first = false;
           else result.append(seperator);
            
           result.append(arr[i].getType());
        }
        
        return result.toString();
    }
    
}
