package com.hahn.basic.definition;

import com.hahn.basic.parser.IEnumExpression;

public enum EnumExpression implements IEnumExpression {
    STMT_EXPRS  ("<CREATE>|<CALL_FUNC>|<MODIFY>", false),
    FACTOR      ("<STMT_EXPRS>|<CAST>|<ANON_FUNC>|<FUNC_POINTER>|<ACCESS>|STRING|CHAR|HEX_NUMBER|NUMBER|TRUE|FALSE|OPEN_PRNTH<EXPRESSION>CLOSE_PRNTH"),
    NEGATION    ("?NOT <FACTOR>"),
    PRODUCT     ("<NEGATION>{MULT_DIV<NEGATION>}"),
    SUMMATION   ("<PRODUCT>{ADD_SUB<PRODUCT>}"),
    BOOLEAN     ("<SUMMATION>{<BOOL_OP><SUMMATION>}"),
    EVAL_CNDTN  ("<BOOLEAN>{<BITWISE><BOOLEAN>}(<TERNARY_OP><EXPRESSION>)"),
    EXPRESSION  ("<EVAL_CNDTN>(<TERNARY_OP><EXPRESSION>)", false),
    
    TERNARY_OP  ("QUESTION|COLON"),
    
    BOOL_OP     ("SC_BITWISE|NOTEQUAL|EQUALS|LESS_EQU|GTR_EQU|LESS|GTR"),
    BITWISE     ("AND|MSC_BITWISE"),
    
    CAST        ("<TYPE>COLON<FACTOR>", false),
    CREATE      ("NEW <TYPE> OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false), 
    
    WHILE_STMT  ("WHILE <CONDITIONAL>", false),
    
    INLINE_IF   ("<EXPRESSION> QUESTION <EXPRESSION> COLON <EXPRESSION>", false),
    IF_STMT     ("IF <CONDITIONAL> {ELSE IF <CONDITIONAL>} (ELSE <BLOCK>)", false),
    CONDITIONAL ("OPEN_PRNTH <EXPRESSION> CLOSE_PRNTH <BLOCK>", false),
    
    FOR_STMT    ("FOR OPEN_PRNTH ?<FOR_DEF> EOL ?<EXPRESSION> EOL (<MODIFY> {COMMA <MODIFY>}) CLOSE_PRNTH <BLOCK>", false),
    FOR_DEF     ("<DEFINE>|<MODIFY>"),
    
    TYPE        ("FUNCTION <PARAM_TYPES>|IDENTIFIER ?<PARAM_TYPES>", false), 
    PARAM_TYPES ("LESS ?<TYPE_LIST> (EOL <TYPE>) GTR", false),
    TYPE_LIST   ("<TYPE> {COMMA <TYPE>}", false),
    
    CALL_FUNC   ("IDENTIFIER OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false),
    CALL_PARAMS ("<EXPRESSION> {COMMA <EXPRESSION>}", false),
    
    FUNC_POINTER("AND IDENTIFIER OPEN_PRNTH ?<TYPE_LIST> CLOSE_PRNTH", false),
    
    ANON_FUNC   ("FUNCTION ?<TYPE> OPEN_PRNTH?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    
    DEF_FUNC    ("FUNCTION <TYPE> IDENTIFIER OPEN_PRNTH ?<DEF_PARAMS> CLOSE_PRNTH <BLOCK>", false),
    DEF_PARAMS  ("<TYPE> IDENTIFIER {COMMA <TYPE> IDENTIFIER}", false),
    
    STRUCT      ("STRUCT IDENTIFIER OPEN_BRACE [<DEFINE> EOL] CLOSE_BRACE", false),
    
    ACCESS      ("IDENTIFIER ?<IN_ACCESS>", false),
    IN_ACCESS   ("OPEN_SQR <EXPRESSION> CLOSE_SQR$|DOT IDENTIFIER$", false),
    
    DEFINE      ("{FLAGS} <TYPE> IDENTIFIER ?<DEF_MODIFY> {COMMA IDENTIFIER ?<DEF_MODIFY>}", false),
    DEF_MODIFY  ("ASSIGN <EXPRESSION>", false),
    
    MODIFY      ("<ACCESS><ASSIGN_OP><EXPRESSION>", false),
    ASSIGN_OP   ("ASSIGN|MODIFY_EQU"),
    
    COMMAND     ("CONTINUE|BREAK|IMPORT"),
    RETURN      ("RETURN ?<EXPRESSION> EOL", false),
    
    BLOCK       ("OPEN_BRACE {<BLOCK_CNTNT>} CLOSE_BRACE|<BLOCK_CNTNT>", false),
    BLOCK_CNTNT ("<DEFINE>EOL|<COMMAND>EOL|<EXPRESSION>EOL|<RETURN>|<IF_STMT>|<WHILE_STMT>|<FOR_STMT>|EOL", false),
    START       ("<BLOCK_CNTNT>$|<DEF_FUNC>$|<STRUCT>$", false);


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
