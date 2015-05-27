package com.hahn.basic.definition;

import com.hahn.basic.parser.IEnumExpression;
import com.hahn.basic.viewer.util.TextColor;

public enum EnumExpression implements IEnumExpression {   
    STMT_EXPRS  ("<CREATE_ARR>|<CREATE_EARR>|<CREATE>|<CALL_FUNC>|<MODIFY>|<ACCESS>", false),
    FACTOR      ("<STMT_EXPRS>|<CAST>|<ANON_FUNC>|<FUNC_POINTER>|NULL|CHAR|HEX_INT|REAL|TRUE|FALSE|OPEN_PRNTH<EXPRESSION>CLOSE_PRNTH"),
    PREFIX_OP   ("ADD_ADD|SUB_SUB|SUB|NOT|TILDE|HASH"),
    PREFIX      ("?<PREFIX_OP> <FACTOR>"),
    PRODUCT     ("<PREFIX>{<MULT_DIV><PREFIX>}"),
    SUMMATION   ("<PRODUCT>{<ADD_SUB><PRODUCT>}"),
    BOOLEAN     ("<SUMMATION>{<BOOL_OP><SUMMATION>}"),
    EVAL_CNDTN  ("<BOOLEAN>{<BITWISE><BOOLEAN>}"),
    EXPRESSION  ("<EVAL_CNDTN>(QUESTION<EXPRESSION>COLON<EXPRESSION>)", false),
    
    MULT_DIV    ("MULT|DIV"),
    ADD_SUB     ("ADD|SUB"),
    BOOL_OP     ("BOOL_AND|BOOL_OR|NOTEQUAL|EQUALS|LESS_EQU|GTR_EQU|LESS|GTR"),
    BITWISE     ("AND|BOR|XOR|RSHIFT|LSHIFT"),
    
    CAST        ("OPEN_PRNTH <TYPE> CLOSE_PRNTH <EXPRESSION>|OPEN_PRNTH <TYPE>COLON<EXPRESSION> CLOSE_PRNTH", false),
    CREATE      ("NEW <TYPE> OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false),
    CREATE_EARR ("NEW <TYPE> [ OPEN_SQR ?<EXPRESSION> CLOSE_SQR ]", false),
    CREATE_ARR  ("OPEN_SQR ?<EXPRESSION> { COMMA ?<EXPRESSION> } CLOSE_SQR", false),
    
    WHILE_STMT  ("WHILE <CONDITIONAL>", false),
    
    INLINE_IF   ("<EXPRESSION> QUESTION <EXPRESSION> COLON <EXPRESSION>", false),
    IF_STMT     ("IF <CONDITIONAL> {ELSE IF <CONDITIONAL>} (ELSE <BLOCK>)", false),
    CONDITIONAL ("OPEN_PRNTH <EXPRESSION> CLOSE_PRNTH <BLOCK>", false),
    
    FOR_STMT    ("FOR OPEN_PRNTH ?<FOR_DEF> EOL ?<EXPRESSION> EOL (<MODIFY> {COMMA <MODIFY>}) CLOSE_PRNTH <BLOCK>", false),
    FOR_DEF     ("<DEFINE>|<MODIFY>"),
    
    TYPE_ID     ("IDENTIFIER|FUNCTION"),
    TYPE        ("<TYPE_ID> ?<PARAM_TYPES> { OPEN_SQR CLOSE_SQR }", false),
    PARAM_TYPES ("LESS ?<TYPE_LIST> (EOL <TYPE>) GTR", false),
    TYPE_LIST   ("<TYPE> {COMMA <TYPE>}", false),
    
    FUNC_POINTER("AND IDENTIFIER OPEN_PRNTH ?<TYPE_LIST> CLOSE_PRNTH", false),
    
    ANON_FUNC   ("FUNCTION ?<TYPE> OPEN_PRNTH ?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    CONSTRUCTOR ("CONSTRUCTOR OPEN_PRNTH ?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    DEF_FUNC    ("{<F_FLAG>} <TYPE> IDENTIFIER OPEN_PRNTH ?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    DEF_PARAMS  ("<TYPE> IDENTIFIER (ASSIGN <EXPRESSION>) {COMMA <TYPE> IDENTIFIER (ASSIGN <EXPRESSION>)}", false),
    F_FLAG      ("STATIC|FINAL|PRIVATE", false),
    
    STRUCT      ("STRUCT IDENTIFIER OPEN_BRACE [<DEFINE> EOL] CLOSE_BRACE", false),
    
    CLASS       ("{<C_FLAG>} CLASS IDENTIFIER {<C_PARENT>} OPEN_BRACE {<CLASS_CNTNT>} CLOSE_BRACE", false),
    C_FLAG      ("ABSTRACT|FINAL", false),
    C_PARENT    ("EXTENDS IDENTIFIER", false),
    
    REGEX       ("DIV * DIV", false),
    
    IDENTIFIER  ("STRING|<REGEX>|IDENTIFIER|THIS|SUPER"),
    ACCESS      ("<IDENTIFIER> ?<IN_ACCESS>", false),
    CALL_FUNC   ("<IDENTIFIER> OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH ?<IN_ACCESS>", false),
    
    IN_ACCESS   ("OPEN_SQR <EXPRESSION> CLOSE_SQR$|DOT IDENTIFIER ?<PRNTH_PARAMS>$", false),
    PRNTH_PARAMS("OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false),
    CALL_PARAMS ("<EXPRESSION> {COMMA <EXPRESSION>}", false),
    
    FLAG        ("CONST|PRIVATE|STATIC", false),
    DEFINE      ("{<FLAG>} <TYPE> IDENTIFIER ?<DEF_MODIFY> {COMMA IDENTIFIER ?<DEF_MODIFY>}|{<FLAG>} <CREATE_EARR> IDENTIFIER {COMMA <CREATE_EARR> IDENTIFIER}", false),
    DEF_MODIFY  ("ASSIGN <EXPRESSION>", false),
    
    ADD2_SUB2   ("ADD_ADD|SUB_SUB"),
    MODIFY      ("<ADD2_SUB2> <ACCESS>|<ACCESS> <ADD2_SUB2>|<ACCESS><ASSIGN_OP><EXPRESSION>", false),
    ASSIGN_OP   ("ASSIGN|PLUS_EQU|SUB_EQU|MULT_EQU|DIV_EQU|AND_EQU|BOR_EQU|XOR_EQU"),
    
    COMMAND     ("CONTINUE|BREAK"),
    RETURN      ("RETURN ?<EXPRESSION>", false),
    
    BLOCK       ("OPEN_BRACE {<BLOCK_CNTNT>} CLOSE_BRACE|<BLOCK_CNTNT>", false),
    /** Stuff that can be done within if(){} */
    BLOCK_CNTNT ("COMMENT|<DEFINE>EOL|<COMMAND>EOL|<EXPRESSION>EOL|<RETURN>EOL|<IF_STMT>|<WHILE_STMT>|<FOR_STMT>|EOL", false),
    /** Stuff that can be done within class{} */
    CLASS_CNTNT ("COMMENT|<DEF_FUNC>|<CONSTRUCTOR>|<DEFINE>EOL|EOL", false),
    
    IMPORT      ("IMPORT IDENTIFIER {DOT IDENTIFIER} EOL", false),
    DIRECTIVE   ("AT IDENTIFIER", false),
    START       ("<DIRECTIVE>$|<IMPORT>$|<STRUCT>$|<CLASS>$|<BLOCK_CNTNT>$|<CLASS_CNTNT>$", false);


    private final String bnf;
    private final boolean canFlatten;
    
    private Enum<?>[][] subexpressions;
    private EnumExpression(String bnf, boolean flatten) {
        this.bnf = bnf;
        this.canFlatten = flatten;
        this.subexpressions = null;
    }
    
    private EnumExpression(String bnf) {
        this(bnf, true);
    }

    @Override
    public void setSubExpressions(Enum<?>[][] se) {
        this.subexpressions = se;
    }
    
    @Override
    public Enum<?>[][] getSubExpressions() {
        return subexpressions;
    }
    
    @Override
    public String getBNFString() {
        return bnf;
    }
    
    @Override
    public boolean canFlatten() {
        return canFlatten;
    }
    
    @Override
    public TextColor getDefaultColor() {
        switch (this) {
        case REGEX:
            return EnumToken.STRING.getDefaultColor();
        default:
            return null;
        }
    }
}
