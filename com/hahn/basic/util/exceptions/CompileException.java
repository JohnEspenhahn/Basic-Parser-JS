package com.hahn.basic.util.exceptions;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.CompilerUtils;

public class CompileException extends RuntimeException {
    private static final long serialVersionUID = 4974149633641094015L;
    
    final int row, col;
    final String message;
    
    public CompileException(String mss, CodeFile file) {
        this(mss, file, file.getCurrentRow(), file.getCurrentColumn());
    }
    
    public CompileException(String mss, Compilable c) {
        this(mss, c.getFile(), c.row, -1);
    }
    
    public CompileException(String mss, Node node) {
        this(mss, node.getFile(), node.getRow(), node.getCol());
    }
    
    public CompileException(String mss, CodeFile file, String badLinePart) {
        this(mss, file, file.getCurrentRow(), file.getLine(file.getCurrentRow()).indexOf(badLinePart));
    }
    
    public CompileException(String mss, CodeFile file, int row, int col) {
        super(mss + " in line " + row + (col < 0 ? "" : "\n" + file.getLine(row - 1).replace('\t', ' ') + "\n" + CompilerUtils.createArrow(' ', '^', col)));
        
        this.row = row;
        this.col = col;
        this.message = mss;
        
        // Main.getInstance().putLineError(this);
    }
    
    public int getRow() {
        return row;
    }
    
    public String getTooltipMessage() {
        return message;
    }
}
