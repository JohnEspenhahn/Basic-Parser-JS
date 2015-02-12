package com.hahn.basic.definition;

import java.util.ArrayList;
import java.util.List;

import com.hahn.basic.lexer.regex.IEnumRegexToken;
import com.hahn.basic.viewer.TextColor;

public enum EnumToken implements IEnumRegexToken {
    STRING      ("\".*?[^\\\\]\""            , Group.LIT),
    HEX_INTEGER ("0x[0-9A-Fa-f]+"            , Group.LIT),
    FLOAT       ("[0-9]+\\.[0-9]*|\\.[0-9]+" , Group.LIT),
    INTEGER     ("[0-9]+"                    , Group.LIT), // [0-9]+\\.?[0-9]*
    CHAR        ("'\\?.'"                    , Group.LIT),
    EQUALS      ("=="      , Group.OP),
    PLUS_EQU    ("\\+="    , Group.OP),
    SUB_EQU     ("\\-="    , Group.OP),
    MULT_EQU    ("\\*="    , Group.OP),
    DIV_EQU     ("/="      , Group.OP),
    AND_EQU     ("&="      , Group.OP),
    BOR_EQU     ("\\|="    , Group.OP),
    XOR_EQU     ("\\^="    , Group.OP),
    NOTEQUAL    ("!="      , Group.OP),
    ASSIGN      ("="       , Group.OP),
    DOT         ("\\."     , Group.OP),
    BOOL_AND    ("&&"      , Group.OP),
    BOOL_OR     ("\\|\\|"  , Group.OP),
    QUESTION    ("\\?"     , Group.OP),
    AND         ("&"       , Group.OP),
    NOT         ("!"       , Group.OP),
    RSHIFT      (">>"      , Group.OP),
    LSHIFT      ("<<"      , Group.OP),
    BOR         ("\\|"     , Group.OP),
    XOR         ("\\^"     , Group.OP),
    ARROW       ("->"      , Group.OP),
    LESS_EQU    ("<="      , Group.OP),
    GTR_EQU     (">="      , Group.OP),
    LESS        ("<"       , Group.OP),
    GTR         (">"       , Group.OP),
    ADD_ADD     ("\\+\\+"  , Group.OP),
    ADD         ("\\+"     , Group.OP),
    SUB_SUB     ("\\-\\-"  , Group.OP),
    SUB         ("\\-"     , Group.OP),
    MULT        ("\\*"     , Group.OP),
    DIV         ("/"       , Group.OP),
    MOD         ("%"       , Group.OP),
    DBL_COLON   ("\\:\\:"  , Group.OP),
    COLON       ("\\:"     , Group.OP),
    HASH        ("#"       , Group.OP),
    COMMA       (","       , Group.SEP),
    EOL         (";"       , Group.SEP),
    OPEN_PRNTH  ("\\("     , Group.SEP), 
    CLOSE_PRNTH ("\\)"     , Group.SEP),
    OPEN_BRACE  ("\\{"     , Group.SEP),
    CLOSE_BRACE ("\\}"     , Group.SEP),
    OPEN_SQR    ("\\["     , Group.SEP),
    CLOSE_SQR   ("\\]"     , Group.SEP),
    CONST       ("const"      , Group.IDENT),
    FINAL       ("final"      , Group.IDENT),
    PRIVATE     ("private"    , Group.IDENT),
    STATIC      ("static"     , Group.IDENT),
    ABSTRACT    ("abstract"   , Group.IDENT),
    IF          ("if"         , Group.IDENT),
    ELSE        ("else"       , Group.IDENT),
    FOR         ("for"        , Group.IDENT),
    WHILE       ("while"      , Group.IDENT),
    CONTINUE    ("continue"   , Group.IDENT),
    BREAK       ("break"      , Group.IDENT),
    RETURN      ("return"     , Group.IDENT),
    FUNCTION    ("func"       , Group.IDENT),
    STRUCT      ("struct"     , Group.IDENT),
    TRUE        ("true"       , Group.IDENT),
    FALSE       ("false"      , Group.IDENT),
    NEW         ("new"        , Group.IDENT),
    NULL        ("null"       , Group.IDENT),
    IMPORT      ("import"     , Group.IDENT),
    CLASS       ("class"      , Group.IDENT),
    CONSTRUCTOR ("constructor", Group.IDENT),
    IMPLEMENTS  ("implements" , Group.IDENT),
    EXTENDS     ("extends"    , Group.IDENT),
    THIS        ("this"       , Group.IDENT),
    SUPER       ("super"      , Group.IDENT),
    
    COMMENT     ("\\\\[^\n]+" , Group.CMNT),
    
    // Special reserved keywords
    /** super     */ __s__       ("__s__"      , Group.IDENT),
    /** construct */ __c__       ("__c__"      , Group.IDENT),
    /** extend    */ __e__       ("__e__"      , Group.IDENT),
    /** name      */ __n__       ("__n__"      , Group.IDENT),
    /** new arr   */ __a__       ("__a__"      , Group.IDENT),
    // End special reserved keywords
    
    IDENTIFIER  ("<<WORD>>"   , Group.IDENT);

    private final int group;
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
        this.group = group;
        
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
        case Group.CMNT:
            break;
        default:
            throw new RuntimeException("Unhandled EnumToken group `" + group + "`");            
        }
    }
    
    @Override
    public String getRegex() {
        return regex;
    }
    
    public TextColor getDefaultColor() {
        switch (group) {
        case Group.IDENT:
            if (this == IDENTIFIER) return TextColor.GREY;
            else return TextColor.YELLOW;
        case Group.LIT:
            if (this == STRING) return TextColor.LIGHT_BLUE;
            else return TextColor.GREY;
        case Group.OP:
            return TextColor.YELLOW;
        case Group.SEP:
            return TextColor.GREY;
        case Group.CMNT:
            return TextColor.GREEN;
        default:
            throw new RuntimeException("Unhandled EnumToken group `" + group + "`");   
        }
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
        /** Comment */
        public static final int CMNT  = 5;
        
        public static final List<EnumToken> identifiers = new ArrayList<EnumToken>();
        public static final List<EnumToken> literals    = new ArrayList<EnumToken>();
        public static final List<EnumToken> operators   = new ArrayList<EnumToken>();
        public static final List<EnumToken> separators   = new ArrayList<EnumToken>();
    }
}
