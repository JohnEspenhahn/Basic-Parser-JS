package com.hahn.basic.util.exceptions;

import com.hahn.basic.parser.Node;

public class CastException extends CompileException {
    private static final long serialVersionUID = 833355693096718661L;

    public CastException(String mss, Node node) {
        super(mss, node);
    }

    public CastException(String mss, Node node, CompileException e) {  
        super(mss + e.message, node);
    }

    public CastException(String mss, int row, int col) {
        super(mss, row, col);
    }
    
}
