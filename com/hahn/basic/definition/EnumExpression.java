package com.hahn.basic.definition;

import com.hahn.basic.parser.IEnumExpression;


public enum EnumExpression implements IEnumExpression {
    FACTOR      ("<ANON_FUNC>|<FUNC_POINTER>|<EVALUABLE>|<ACCESS>|STRING|CHAR|HEX_NUMBER|NUMBER|TRUE|FALSE|OPEN_PRNTH<EXPRESSION>CLOSE_PRNTH"),
    PRODUCT     ("<FACTOR>{MULT_DIV<FACTOR>}"),
    SUMMATION   ("<PRODUCT>{ADD_SUB<PRODUCT>}"),
    BOOLEAN     ("<SUMMATION>{<BOOL_OP><SUMMATION>}"),
    EVAL_CNDTN  ("<BOOLEAN>{<BITWISE><BOOLEAN>}", false),
    EXPRESSION  ("?<CAST><EVAL_CNDTN>", false),
    
    CAST        ("<TYPE> COLON", false),
    
    BOOL_OP     ("NOTEQUAL|EQUALS|LESS_EQU|GTR_EQU|LESS|GTR"),
    BITWISE     ("AND|MSC_BITWISE"),
    
    CREATE      ("NEW <TYPE> OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false),    
    DELETE      ("DELETE <ACCESS>", false),
    
    EVALUABLE   ("<CREATE>|<DELETE>|<CALL_FUNC>"),  
    
    WHILE_STMT  ("WHILE <CONDITIONAL>", false),
    IF_STMT     ("IF <CONDITIONAL> {ELSE IF <CONDITIONAL>} (ELSE <BLOCK>)", false),
    CONDITIONAL ("OPEN_PRNTH <EXPRESSION> CLOSE_PRNTH <BLOCK>", false),
    
    FOR_STMT    ("FOR OPEN_PRNTH ?<FOR_DEF> EOL ?<EXPRESSION> EOL (<MODIFY> {COMMA <MODIFY>}) CLOSE_PRNTH <BLOCK>", false),
    FOR_DEF     ("<DEFINE>|<MODIFY>"),
    
    TYPE        ("IDENTIFIER ?<PARAM_TYPES>", false), 
    PARAM_TYPES ("LESS ?<TYPE_LIST> (EOL <TYPE>) GTR", false),
    TYPE_LIST   ("<TYPE> {COMMA <TYPE>}", false),
    
    CALL_FUNC   ("IDENTIFIER OPEN_PRNTH ?<CALL_PARAMS> CLOSE_PRNTH", false),
    CALL_PARAMS ("<EXPRESSION> {COMMA <EXPRESSION>}", false),
    
    FUNC_POINTER("AND IDENTIFIER OPEN_PRNTH ?<TYPE_LIST> CLOSE_PRNTH", false),
    
    ANON_FUNC   ("FUNCTION ?<DEF_PARAMS> ARROW ?<TYPE> <BLOCK>", false),
    
    DEF_FUNC    ("IDENTIFIER ?<DEF_PARAMS> ARROW ?<TYPE> <BLOCK>", false),
    DEF_PARAMS  ("<TYPE> IDENTIFIER {COMMA <TYPE> IDENTIFIER}", false),
    
    STRUCT      ("STRUCT IDENTIFIER OPEN_BRACE [<DEFINE> EOL] CLOSE_BRACE", false),
    
    ACCESS      ("IDENTIFIER ?<IN_ACCESS>", false),
    IN_ACCESS   ("OPEN_SQR <EXPRESSION> CLOSE_SQR$|DOT IDENTIFIER$", false),
    
    DEFINE_G    ("GLOBAL <DEFINE>", false),
    DEFINE      ("<TYPE> IDENTIFIER ?<DEF_MODIFY> {COMMA IDENTIFIER ?<DEF_MODIFY>}", false),
    DEF_MODIFY  ("ASSIGN <EXPRESSION>", false),
    
    MODIFY      ("<ACCESS><ASSIGN_OP><EXPRESSION>", false),
    ASSIGN_OP   ("ASSIGN|MODIFY_EQU"),
    
    COMMAND     ("CONTINUE|BREAK|IMPORT"),
    RETURN      ("RETURN ?<EXPRESSION> EOL", false),
    
    BLOCK       ("OPEN_BRACE {<BLOCK_CNTNT>} CLOSE_BRACE|<BLOCK_CNTNT>", false),
    BLOCK_CNTNT ("<DEFINE>EOL|<MODIFY>EOL|<COMMAND>EOL|<EVALUABLE>EOL|<RETURN>|<IF_STMT>|<WHILE_STMT>|<FOR_STMT>|EOL", false),
    START       ("<DEFINE_G>$|<BLOCK_CNTNT>$|<DEF_FUNC>$|<STRUCT>$", false);


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