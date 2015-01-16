package com.hahn.basic.definition;

import com.hahn.basic.parser.IEnumExpression;

public enum EnumExpression implements IEnumExpression {   
    STMT_EXPRS  ("<CREATE>|<CREATE_ARR>|<CALL_FUNC>|<MODIFY>", false),
    FACTOR      ("<STMT_EXPRS>|<CAST>|<ANON_FUNC>|<FUNC_POINTER>|<ACCESS>|NULL|CHAR|HEX_INTEGER|INTEGER|FLOAT|TRUE|FALSE|OPEN_PRNTH<EXPRESSION>CLOSE_PRNTH"),
    PREFIX_OP   ("ADD_ADD|SUB_SUB|ADD|SUB|NOT"),
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
    
    CAST        ("OPEN_PRNTH <TYPE>COLON<FACTOR> CLOSE_PRNTH", false),
    CREATE      ("NEW <TYPE> OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false), 
    CREATE_ARR  ("NEW <TYPE> [ OPEN_BRACE ?<EXPRESSION> CLOSE_BRACE ]", false),
    
    WHILE_STMT  ("WHILE <CONDITIONAL>", false),
    
    INLINE_IF   ("<EXPRESSION> QUESTION <EXPRESSION> COLON <EXPRESSION>", false),
    IF_STMT     ("IF <CONDITIONAL> {ELSE IF <CONDITIONAL>} (ELSE <BLOCK>)", false),
    CONDITIONAL ("OPEN_PRNTH <EXPRESSION> CLOSE_PRNTH <BLOCK>", false),
    
    FOR_STMT    ("FOR OPEN_PRNTH ?<FOR_DEF> EOL ?<EXPRESSION> EOL (<MODIFY> {COMMA <MODIFY>}) CLOSE_PRNTH <BLOCK>", false),
    FOR_DEF     ("<DEFINE>|<MODIFY>"),
    
    TYPE_ID     ("IDENTIFIER|FUNCTION"),
    TYPE        ("<TYPE_ID> ?<PARAM_TYPES> { OPEN_BRACE CLOSE_BRACE }", false),
    PARAM_TYPES ("LESS ?<TYPE_LIST> (EOL <TYPE>) GTR", false),
    TYPE_LIST   ("<TYPE> {COMMA <TYPE>}", false),
    
    CALL_FUNC   ("<ACCESS> OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false),
    CALL_PARAMS ("<EXPRESSION> {COMMA <EXPRESSION>}", false),
    
    FUNC_POINTER("AND IDENTIFIER OPEN_PRNTH ?<TYPE_LIST> CLOSE_PRNTH", false),
    
    ANON_FUNC   ("FUNCTION ?<TYPE> OPEN_PRNTH ?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    CONSTRUCTOR ("CONSTRUCTOR OPEN_PRNTH ?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    DEF_FUNC    ("{<F_FLAG>} <TYPE> IDENTIFIER OPEN_PRNTH ?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    DEF_PARAMS  ("<TYPE> IDENTIFIER (ASSIGN <EXPRESSION>) {COMMA <TYPE> IDENTIFIER (ASSIGN <EXPRESSION>)}", false),
    F_FLAG      ("FINAL|PRIVATE", false),
    
    STRUCT      ("STRUCT IDENTIFIER OPEN_BRACE [<DEFINE> EOL] CLOSE_BRACE", false),
    
    CLASS       ("{<C_FLAG>} CLASS IDENTIFIER {<C_PARENT>} OPEN_BRACE {<CLASS_CNTNT>} CLOSE_BRACE", false),
    C_FLAG      ("ABSTRACT|FINAL", false),
    C_PARENT    ("EXTENDS IDENTIFIER", false),
    
    IDENTIFIER  ("STRING|IDENTIFIER|THIS|SUPER"),
    ACCESS      ("<IDENTIFIER> ?<IN_ACCESS>", false),
    IN_ACCESS   ("OPEN_SQR <EXPRESSION> CLOSE_SQR$|DOT IDENTIFIER$", false),
    
    FLAG        ("CONST|PRIVATE|STATIC", false),
    DEFINE      ("{<FLAG>} <TYPE> IDENTIFIER ?<DEF_MODIFY> {COMMA IDENTIFIER ?<DEF_MODIFY>}", false),
    DEF_MODIFY  ("ASSIGN <EXPRESSION>", false),
    
    ADD2_SUB2   ("ADD_ADD|SUB_SUB"),
    MODIFY      ("<ADD2_SUB2> <ACCESS>|<ACCESS> <ADD2_SUB2>|<ACCESS><ASSIGN_OP><EXPRESSION>", false),
    ASSIGN_OP   ("ASSIGN|PLUS_EQU|SUB_EQU|MULT_EQU|DIV_EQU|AND_EQU|BOR_EQU|XOR_EQU"),
    
    COMMAND     ("CONTINUE|BREAK|IMPORT"),
    RETURN      ("RETURN ?<EXPRESSION> EOL", false),
    
    BLOCK       ("OPEN_BRACE {<BLOCK_CNTNT>} CLOSE_BRACE|<BLOCK_CNTNT>", false),
    /** Stuff that can be done within if(){} */
    BLOCK_CNTNT ("<DEFINE>EOL|<COMMAND>EOL|<EXPRESSION>EOL|<RETURN>|<IF_STMT>|<WHILE_STMT>|<FOR_STMT>|EOL", false),
    /** Stuff that can be done within class{} */
    CLASS_CNTNT ("<DEF_FUNC>|<CONSTRUCTOR>|<DEFINE>EOL|EOL", false),
    
    DIRECTIVE   ("HASH IDENTIFIER", false),
    START       ("<DIRECTIVE>$|<STRUCT>$|<CLASS>$|<BLOCK_CNTNT>$|<CLASS_CNTNT>$", false);


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

    public void setSubExpressions(Enum<?>[][] se) {
        this.subexpressions = se;
    }
    
    public Enum<?>[][] getSubExpressions() {
        return subexpressions;
    }
    
    public String getBNFString() {
        return bnf;
    }
    
    public boolean canFlatten() {
        return canFlatten;
    }
}
