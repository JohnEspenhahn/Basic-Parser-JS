package com.hahn.basic.definition;

import com.hahn.basic.lexer.IEnumToken;

public enum EnumToken implements IEnumToken {
    EOL         (";"                         ),
    STRING      ("\".+?\""                   ),
    HEX_NUMBER  ("0x[0-9A-Fa-f]+"            ),
    NUMBER      ("[0-9]+"                    ), // [0-9]+\\.?[0-9]*
    CHAR        ("'.'"                       ),
    EQUALS      ("=="                        ),
    MODIFY_EQU  ("[\\+\\-\\*/&\\|\\^]="      ),
    NOTEQUAL    ("!="                        ),
    ASSIGN      ("="                         ),
    COMMA       (","                         ),
    DOT         ("\\."                       ),
    AND         ("&"                         ),
    MSC_BITWISE ("<<|>>|\\||\\^"             ),
    ARROW       ("->"                        ),
    LESS_EQU    ("<="                        ),
    GTR_EQU     (">="                        ),
    LESS        ("<"                         ),
    GTR         (">"                         ),
    ADD_SUB     ("\\+|\\-"                   ),
    MULT_DIV    ("\\*|/|%"                   ),
    OPEN_PRNTH  ("\\("                       ), 
    CLOSE_PRNTH ("\\)"                       ),
    OPEN_BRACE  ("\\{"                       ),
    CLOSE_BRACE ("\\}"                       ),
    OPEN_SQR    ("\\["                       ),
    CLOSE_SQR   ("\\]"                       ),
    DBL_COLON   ("\\:\\:"                    ),
    COLON       ("\\:"                       ),
    IF          ("if"                        ),
    ELSE        ("else"                      ),
    FOR         ("for"                       ),
    WHILE       ("while"                     ),
    CONTINUE    ("continue"                  ),
    BREAK       ("break"                     ),
    RETURN      ("return"                    ),
    FUNCTION    ("function"                  ),
    STRUCT      ("struct"                    ),
    TRUE        ("true"                      ),
    FALSE       ("false"                     ),
    NEW         ("new"                       ),
    DELETE      ("delete"                    ),
    GLOBAL      ("global"                    ),
    IMPORT      ("import [_a-zA-Z][_a-zA-Z0-9\\.]*" ),
    IDENTIFIER  ("<<WORD>>"                  );

    
    private final String regex;
    private EnumToken(String r) { this.regex = r; }
    
    @Override
    public String getRegex() {
        return regex;
    }
}
