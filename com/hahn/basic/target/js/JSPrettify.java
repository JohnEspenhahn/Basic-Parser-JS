package com.hahn.basic.target.js;

import org.apache.commons.lang3.StringUtils;

public class JSPrettify {    
    private static int indent = 0;
    
    public static String pretty(String str) {
        return StringUtils.repeat(TAB, indent) + str;
    }
    
    
    public static final String TAB = "    ";
}
