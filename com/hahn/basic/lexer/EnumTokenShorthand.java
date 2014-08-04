package com.hahn.basic.lexer;

/**
 * Shorthand expressions for tokens. Some are also
 * used to get correct index when displaying error
 * messages
 * 
 * @author John Espenhahn
 *
 */
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
