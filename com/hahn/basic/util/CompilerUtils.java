package com.hahn.basic.util;

import java.util.Arrays;
import java.util.Iterator;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.parser.Node;

public class CompilerUtils {
    
    public static String getListSeperator() {
        return (Main.getInstance().isPretty() ? ", " : ",");
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
    
    /**
     * Get an iterator of the node's children
     * @param n The node
     * @return An iterator of the node's children
     */
    public static Iterator<Node> getIterator(Node n) {
        return n.getAsChildren().iterator();
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