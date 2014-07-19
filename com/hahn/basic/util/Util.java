package com.hahn.basic.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;


public class Util {

    public static String repeat(String str, String delim, int num) {
        StringBuffer result = new StringBuffer(str.length() * num + delim.length() * (num-1));
        for (int i = 0; i < num; i++) {
            result.append(str);
            
            if (i + 1 < num) {
                result.append(delim);
            }
        }
        
        return result.toString();
    }
    
    public static String toString(int[] arr, String seperator) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
           result.append(arr[i]);
           
           if (i + 1 < arr.length) {
               result.append(seperator);
           }
        }
        
        return result.toString();
    }
    
    public static String toString(Object[] arr, String seperator) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
           result.append(arr[i].toString());
           
           if (i + 1 < arr.length) {
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
    
    public static int[] chars2intarr(String str) {
        int[] arr = new int[1 + str.length()];
        arr[0] = str.length();
        for (int i = 0; i < str.length(); i++) {
            arr[i+1] = str.charAt(i);
        }
        
        return arr;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map arr2map(Class<?> key, Class<?> value, Object[][] arr) {
        Map map = new HashMap();
        
        for (Object[] pair: arr) {
            if (pair.length != 2) {
                throw new RuntimeException("Invalid pair '" + pair + "'");
            }
            
            map.put(key.cast(pair[0]), value.cast(pair[1]));
        }
        
        return map;
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
    
    public static boolean IsPowerOfTwo(int x) {
        return (x != 0) && ((x & (x - 1)) == 0);
    }

    public static String splitChars(String value, String delim) {
        StringBuilder str = new StringBuilder(value.length() * 3 + ((value.length() - 1) * delim.length()));
        for (int i = 0; i < value.length(); i++) {
            str.append((int) value.charAt(i));
            if (i < value.length() - 1) {
                str.append(delim);
            }
        }
        
        return str.toString();
    }

    public static String toHexStr(String str) {
        return String.format("%x", new BigInteger(1, str.getBytes()));
    }
    
    public static boolean toBool(double d) {
		return (d != 0 ? true : false);
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