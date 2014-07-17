package com.hahn.basic.lexer;

public enum EnumTokenShorthand {
    WORD("[_$a-zA-Z][_$a-zA-Z0-9]*");
    
    public final String Regex;
    private EnumTokenShorthand(String regex) {
        Regex = regex;
    }
    
    @Override
    public String toString() {
        return "<<" + name() + ">>";
    }
}
