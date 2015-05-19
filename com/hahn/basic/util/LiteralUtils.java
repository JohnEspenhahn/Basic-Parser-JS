package com.hahn.basic.util;

import java.math.BigInteger;

import org.apache.commons.lang3.math.NumberUtils;

import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.parser.Node;

public class LiteralUtils {
    
    public static LiteralNum parseInt(Node n) {
        if (n.getValue().startsWith("0x")) {
            return new LiteralNum(Integer.valueOf(n.getValue().substring(2), 16));
        } else if (n.getValue().startsWith("'")) {
            return new LiteralNum(n.getValue().charAt(1));
        } else {
            return new LiteralNum(Integer.valueOf(n.getValue()));
        }
    }
    
    public static LiteralNum parseFloat(Node n) {
        return new LiteralNum(Double.valueOf(n.getValue()));
    }
    
    public static boolean toBool(double d) {
        return (d != NumberUtils.DOUBLE_ZERO ? true : false);
    }

    public static boolean isPowerOfTwo(int x) {        
        return (x != 0) && ((x & (x - 1)) == 0);
    }
    
    public static String toHexStr(String str) {
        return String.format("%x", new BigInteger(1, str.getBytes()));
    }
    
}
