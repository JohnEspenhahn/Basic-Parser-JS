package com.hahn.basic.util.exceptions;

import com.hahn.basic.intermediate.CodeFile;

public class LexException extends CompileException {
    private static final long serialVersionUID = 1628678990717984757L;

    public LexException(CodeFile file, int row, int col) {
        super("Invalid token", file, row, col);
    }
    
}
