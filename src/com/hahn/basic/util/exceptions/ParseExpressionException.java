package com.hahn.basic.util.exceptions;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.parser.Node;

public class ParseExpressionException extends CompileException {
    private static final long serialVersionUID = -775631702779674403L;

    public ParseExpressionException(Node node) {
        this(node.getFile(), node.getRow(), node.getCol());
    }
    
    public ParseExpressionException(CodeFile file, int row, int col) {
        super("Invalid expression near index " + col, file, row, col);
    }
    
}
