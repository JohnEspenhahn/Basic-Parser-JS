package com.hahn.basic.lexer.basic;

import java.util.ArrayList;
import java.util.List;

public enum EnumBasicToken {
    EOL         (";"        ),
    QUOTE       ("\""       ),
    APOSTROPHE  ("'"        ),
    HEX_INTEGER (""         ),
    INTEGER     (""         ), // [0-9]+\\.?[0-9]*
    FLOAT       (""         ),
    CHAR        ("'.'"      ),
    EQUALS      ("=="       ),
    PLUS_EQU    ("+="       ),
    SUB_EQU     ("-="       ),
    MULT_EQU    ("*="       ),
    DIV_EQU     ("/="       ),
    AND_EQU     ("&="       ),
    BOR_EQU     ("|="       ),
    XOR_EQU     ("^="       ),
    NOTEQUAL    ("!="       ),
    ASSIGN      ("="        ),
    COMMA       (","        ),
    DOT         ("."        ),
    BOOL_AND    ("&&"       ),
    BOOL_OR     ("||"       ),
    QUESTION    ("?"        ),
    AND         ("&"        ),
    NOT         ("!"        ),
    RSHIFT      (">>"       ),
    LSHIFT      ("<<"       ),
    BOR         ("|"        ),
    XOR         ("^"        ),
    ARROW       ("->"       ),
    LESS_EQU    ("<="       ),
    GTR_EQU     (">="       ),
    LESS        ("<"        ),
    GTR         (">"        ),
    ADD         ("+"        ),
    SUB         ("-"        ),
    MULT        ("*"        ),
    DIV         ("/"        ),
    MOD         ("%"        ),
    OPEN_PRNTH  ("("        ), 
    CLOSE_PRNTH (")"        ),
    OPEN_BRACE  ("{"        ),
    CLOSE_BRACE ("}"        ),
    OPEN_SQR    ("["        ),
    CLOSE_SQR   ("]"        ),
    COLON       (":"        ),
    CONST       ("const"    ),
    IF          ("if"       ),
    ELSE        ("else"     ),
    FOR         ("for"      ),
    WHILE       ("while"    ),
    CONTINUE    ("continue" ),
    BREAK       ("break"    ),
    RETURN      ("return"   ),
    FUNCTION    ("func"     ),
    STRUCT      ("struct"   ),
    TRUE        ("true"     ),
    FALSE       ("false"    ),
    NEW         ("new"      ),
    IMPORT      ("import"   ),
    IDENTIFIER  (""         );

    
    private final String str;
    private EnumBasicToken(String s) { this.str = s; }
    
    /**
     * Get the string that identifies this token
     * @return The identifying string (ex: "]" for CLOSE_SQR)
     */
    public String getString() {
        return str;
    }
    
    public static boolean isIdentifier(EnumBasicToken t) {
        for (EnumBasicToken identifier: identifiers) {
            if (t == identifier) return true;
        }
        
        return false;
    }
    
    public static final EnumBasicToken[] identifiers = new EnumBasicToken[] {
        CONST, IF, ELSE, FOR, WHILE, CONTINUE, BREAK, RETURN, FUNCTION, STRUCT, TRUE, FALSE, NEW, IMPORT
    };
    
    public static final List<EnumBasicToken> nonIdentifiers = new ArrayList<EnumBasicToken>(EnumBasicToken.values().length);
    static {
        for (EnumBasicToken t: EnumBasicToken.values()) {
            if (!isIdentifier(t)) {
                nonIdentifiers.add(t);
            }
        }
    }
}
