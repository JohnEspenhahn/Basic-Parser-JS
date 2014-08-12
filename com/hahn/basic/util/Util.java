package com.hahn.basic.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;


public class Util {
    
    public static String getListSeperator() {
        return (Main.PRETTY_PRINT ? ", " : ",");
    }
    
    public static String joinTypes(ITypeable[] arr, char seperator) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
           result.append(arr[i].getType());
           
           if (i + 1 < arr.length) {
               result.append(seperator);
           }
        }
        
        return result.toString();
    }
    
    public static String toTarget(IIntermediate[] arr) {
        return toTarget(arr, getListSeperator());
    }
    
    public static String toTarget(IIntermediate[] arr, String seperator) {
        return toTarget(Arrays.asList(arr), seperator);
    }
    
    public static String toTarget(Iterable<? extends IIntermediate> arr, String seperator) {        
        Iterator<? extends IIntermediate> it = arr.iterator();
        StringBuffer result = new StringBuffer();
        while (it.hasNext()) {
           result.append(it.next().toTarget());
           
           if (it.hasNext()) {
               result.append(seperator);
           }
        }
        
        return result.toString();
    }
    
    public static String getTypes(ITypeable[] arr, String seperator) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
           result.append(arr[i].getType().toString());
           
           if (i + 1 < arr.length) {
               result.append(seperator);
           }
        }
        
        return result.toString();
    }
    
    public static Type[] getTypes(ITypeable[] params) {
        Type[] arr = new Type[params.length];
        for (int i = 0; i < params.length; i++) {
            arr[i] = params[i].getType();
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
    
    public static Iterator<Node> getIterator(Node n) {
        return n.getAsChildren().iterator();
    }
    
    public static LiteralNum parseInt(Node n) {
        if (n.getValue().startsWith("0x")) {
            return new LiteralNum(Integer.valueOf(n.getValue().substring(2), 16));
        } else if (n.getValue().startsWith("'")) {
            return new LiteralNum((char) n.getValue().charAt(1));
        } else {
            return new LiteralNum(Integer.valueOf(n.getValue()));
        }
    }
    
    public static LiteralNum parseFloat(Node n) {
        return new LiteralNum(Double.valueOf(n.getValue()));
    }
    
    public static boolean isPowerOfTwo(int x) {        
        return (x != 0) && ((x & (x - 1)) == 0);
    }

    public static String toHexStr(String str) {
        return String.format("%x", new BigInteger(1, str.getBytes()));
    }
    
    public static boolean toBool(double d) {
		return (d != NumberUtils.DOUBLE_ZERO ? true : false);
	}

    public static String createArrow(char main, char pointer, int length) {
        StringBuilder str = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            str.append(main);
        }
        str.append(pointer);
        
        return str.toString();
    }
}