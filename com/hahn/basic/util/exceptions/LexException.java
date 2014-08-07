package com.hahn.basic.util.exceptions;

public class LexException extends CompileException {
    private static final long serialVersionUID = 1628678990717984757L;

    public LexException(int row, int col) {
        super("Invalid token", row, col);
    }
    
}
