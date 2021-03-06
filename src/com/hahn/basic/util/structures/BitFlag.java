package com.hahn.basic.util.structures;

import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public enum BitFlag {
    CONST   (0b00000000000000000000000000000001),
    PRIVATE (0b00000000000000000000000000000010),
    FINAL   (0b00000000000000000000000000000100),
    SYSTEM  (0b00000000000000000000000000001000),
    ABSTRACT(0b00000000000000000000000000010000),
    STATIC  (0b00000000000000000000000000100000);
    
    public final int b;
    private BitFlag(int i) {
        this.b = i;
    }
    
    /**
     * Get the flag from the given EnumExpression.FLAG node
     * @param node EnumExpression.FLAG
     * @return The flag
     * @throws CompileException if not a valid flag
     */
    public static int valueOf(Node node) {
        return BitFlag.valueOf(node.getAsChildren().get(0).getValue().toUpperCase()).b;
    }
    
    public static String asString(int flag) {
        String str = "";
        for (BitFlag f: BitFlag.values()) {
            if ((f.b & flag) != 0) {
                str += f.name().toLowerCase() + " ";
            }
        }
        return str;
    }
}