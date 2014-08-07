package com.hahn.basic.definition;

import com.hahn.basic.lexer.regex.IEnumRegexToken;

public enum EnumRegexToken implements IEnumRegexToken {
    STRING      ("\".*?[^\\\\]\""            ),
    HEX_INTEGER ("0x[0-9A-Fa-f]+"            ),
    FLOAT       ("[0-9]+\\.[0-9]*|\\.[0-9]+" ),
    INTEGER     ("[0-9]+"                    ), // [0-9]+\\.?[0-9]*
    CHAR        ("'\\?.'"                    ),
    EOL         (";"         ),
    EQUALS      ("=="        ),
    PLUS_EQU    ("\\+="      ),
    SUB_EQU     ("\\-="      ),
    MULT_EQU    ("\\*="      ),
    DIV_EQU     ("/="        ),
    AND_EQU     ("&="        ),
    BOR_EQU     ("\\|="      ),
    XOR_EQU     ("\\^="      ),
    NOTEQUAL    ("!="        ),
    ASSIGN      ("="         ),
    COMMA       (","         ),
    DOT         ("\\."       ),
    BOOL_AND    ("&&"        ),
    BOOL_OR     ("\\|\\|"    ),
    QUESTION    ("\\?"       ),
    AND         ("&"         ),
    NOT         ("!"         ),
    RSHIFT      (">>"        ),
    LSHIFT      ("<<"        ),
    BOR         ("\\|"       ),
    XOR         ("\\^"       ),
    ARROW       ("->"        ),
    LESS_EQU    ("<="        ),
    GTR_EQU     (">="        ),
    LESS        ("<"         ),
    GTR         (">"         ),
    ADD         ("\\+"       ),
    SUB         ("\\-"       ),
    MULT        ("\\*"       ),
    DIV         ("/"         ),
    MOD         ("%"         ),
    OPEN_PRNTH  ("\\("       ), 
    CLOSE_PRNTH ("\\)"       ),
    OPEN_BRACE  ("\\{"       ),
    CLOSE_BRACE ("\\}"       ),
    OPEN_SQR    ("\\["       ),
    CLOSE_SQR   ("\\]"       ),
    DBL_COLON   ("\\:\\:"    ),
    COLON       ("\\:"       ),
    CONST       ("const"     ),
    IF          ("if"        ),
    ELSE        ("else"      ),
    FOR         ("for"       ),
    WHILE       ("while"     ),
    CONTINUE    ("continue"  ),
    BREAK       ("break"     ),
    RETURN      ("return"    ),
    FUNCTION    ("func"      ),
    STRUCT      ("struct"    ),
    TRUE        ("true"      ),
    FALSE       ("false"     ),
    NEW         ("new"       ),
    IMPORT      ("import [_a-zA-Z][_a-zA-Z0-9\\.]*" ),
    IDENTIFIER  ("<<WORD>>"  );

    
    private final String regex;
    private EnumRegexToken(String r) { this.regex = r; }
    
    @Override
    public String getRegex() {
        return regex;
    }
}
