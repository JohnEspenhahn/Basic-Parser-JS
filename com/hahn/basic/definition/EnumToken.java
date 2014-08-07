package com.hahn.basic.definition;

import java.util.ArrayList;
import java.util.List;

import com.hahn.basic.lexer.regex.IEnumRegexToken;

public enum EnumToken implements IEnumRegexToken {
    STRING      ("\".*?[^\\\\]\""            , Group.LIT),
    HEX_INTEGER ("0x[0-9A-Fa-f]+"            , Group.LIT),
    FLOAT       ("[0-9]+\\.[0-9]*|\\.[0-9]+" , Group.LIT),
    INTEGER     ("[0-9]+"                    , Group.LIT), // [0-9]+\\.?[0-9]*
    CHAR        ("'\\?.'"                    , Group.LIT),
    EQUALS      ("=="        , Group.OP),
    PLUS_EQU    ("\\+="      , Group.OP),
    SUB_EQU     ("\\-="      , Group.OP),
    MULT_EQU    ("\\*="      , Group.OP),
    DIV_EQU     ("/="        , Group.OP),
    AND_EQU     ("&="        , Group.OP),
    BOR_EQU     ("\\|="      , Group.OP),
    XOR_EQU     ("\\^="      , Group.OP),
    NOTEQUAL    ("!="        , Group.OP),
    ASSIGN      ("="         , Group.OP),
    DOT         ("\\."       , Group.OP),
    BOOL_AND    ("&&"        , Group.OP),
    BOOL_OR     ("\\|\\|"    , Group.OP),
    QUESTION    ("\\?"       , Group.OP),
    AND         ("&"         , Group.OP),
    NOT         ("!"         , Group.OP),
    RSHIFT      (">>"        , Group.OP),
    LSHIFT      ("<<"        , Group.OP),
    BOR         ("\\|"       , Group.OP),
    XOR         ("\\^"       , Group.OP),
    ARROW       ("->"        , Group.OP),
    LESS_EQU    ("<="        , Group.OP),
    GTR_EQU     (">="        , Group.OP),
    LESS        ("<"         , Group.OP),
    GTR         (">"         , Group.OP),
    ADD         ("\\+"       , Group.OP),
    SUB         ("\\-"       , Group.OP),
    MULT        ("\\*"       , Group.OP),
    DIV         ("/"         , Group.OP),
    MOD         ("%"         , Group.OP),
    DBL_COLON   ("\\:\\:"    , Group.OP),
    COLON       ("\\:"       , Group.OP),
    COMMA       (","         , Group.SEP),
    EOL         (";"         , Group.SEP),
    OPEN_PRNTH  ("\\("       , Group.SEP), 
    CLOSE_PRNTH ("\\)"       , Group.SEP),
    OPEN_BRACE  ("\\{"       , Group.SEP),
    CLOSE_BRACE ("\\}"       , Group.SEP),
    OPEN_SQR    ("\\["       , Group.SEP),
    CLOSE_SQR   ("\\]"       , Group.SEP),
    CONST       ("const"     , Group.IDENT),
    IF          ("if"        , Group.IDENT),
    ELSE        ("else"      , Group.IDENT),
    FOR         ("for"       , Group.IDENT),
    WHILE       ("while"     , Group.IDENT),
    CONTINUE    ("continue"  , Group.IDENT),
    BREAK       ("break"     , Group.IDENT),
    RETURN      ("return"    , Group.IDENT),
    FUNCTION    ("func"      , Group.IDENT),
    STRUCT      ("struct"    , Group.IDENT),
    TRUE        ("true"      , Group.IDENT),
    FALSE       ("false"     , Group.IDENT),
    NEW         ("new"       , Group.IDENT),
    IMPORT      ("import"    , Group.IDENT),
    CLASS       ("class"     , Group.IDENT),
    IDENTIFIER  ("<<WORD>>"  , Group.IDENT);

    
    private final String regex, str;
    private EnumToken(String r, int group) { 
        this.regex = r;
    
        boolean escaped = false;
        StringBuilder builder = new StringBuilder(r.length());
        for (int i = 0; i < r.length(); i++) {
            char c = r.charAt(i);
            if (!escaped && c == '\\') {
                escaped = true;
            } else {
                escaped = false;
                builder.append(c);
            }
        }
        
        this.str = builder.toString();
        
        switch (group) {
        case Group.IDENT:
            Group.identifiers.add(this);
            break;
        case Group.LIT:
            Group.literals.add(this);
            break;
        case Group.OP:
            Group.operators.add(this);
            break;
        case Group.SEP:
            Group.separators.add(this);
            break;
        default:
            throw new RuntimeException("Unhandled EnumToken group `" + group + "`");            
        }
    }
    
    @Override
    public String getRegex() {
        return regex;
    }
    
    /**
     * Get the string that identifies this token with extra
     * regular expression's "\" removed
     * @return The identifying string (ex: "]" for CLOSE_SQR)
     */
    public String getString() {
        return str;
    }
    
    public static class Group {
        /** Identifier */
        public static final int IDENT = 1;
        /** Literal */
        public static final int LIT   = 2;
        /** Operator (grouping) */
        public static final int OP    = 3;
        /** Separator (non-grouping) */
        public static final int SEP   = 4;
        
        public static final List<EnumToken> identifiers = new ArrayList<EnumToken>();
        public static final List<EnumToken> literals    = new ArrayList<EnumToken>();
        public static final List<EnumToken> operators   = new ArrayList<EnumToken>();
        public static final List<EnumToken> separators   = new ArrayList<EnumToken>();
    }
}
