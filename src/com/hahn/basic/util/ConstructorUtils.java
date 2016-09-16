package com.hahn.basic.util;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.function.FuncHead;

public class ConstructorUtils {
    
    public static String getConstructorName() {
        return EnumToken.CONSTRUCTOR.getString();
    }
    
    public static boolean isConstructorName(String name) {
        return name.equals(getConstructorName());
    }
    
    public static boolean isDefaultConstructor(FuncHead func) {
        return func.getParams().length == 0 && func.getName().equals(getConstructorName());
    }
    
}
