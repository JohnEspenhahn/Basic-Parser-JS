package com.hahn.basic.util.exceptions;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class CompileException extends RuntimeException {
    private static final long serialVersionUID = 4974149633641094015L;
    
    final String message;
    
    public CompileException(String mss) {
        this(mss, Main.getRow(), Main.getCol());
    }
    
    public CompileException(String mss, Compilable c) {
        this(mss, c.row, -1);
    }
    
    public CompileException(String mss, Node node) {
        this(mss, node.getRow(), node.getCol());
    }
    
    public CompileException(String mss, String badLinePart) {
        this(mss, Main.getRow(), Main.getLineStr().indexOf(badLinePart));
    }
    
    public CompileException(String mss, int row, int col) {
        super(mss + " in line " + row + (col < 0 ? "" : "\n" + Main.getLineStr() + "\n" + Util.createArrow(' ', '^', col)));
        
        this.message = mss;
    }
}
