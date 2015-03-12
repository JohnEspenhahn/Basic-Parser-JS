package com.hahn.basic.util.exceptions;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class CompileException extends RuntimeException {
    private static final long serialVersionUID = 4974149633641094015L;
    
    final int row, col;
    final String message;
    
    public CompileException(String mss) {
        this(mss, Main.getInstance().getRow(), Main.getInstance().getCol());
    }
    
    public CompileException(String mss, Compilable c) {
        this(mss, c.row, -1);
    }
    
    public CompileException(String mss, Node node) {
        this(mss, node.getRow(), node.getCol());
    }
    
    public CompileException(String mss, String badLinePart) {
        this(mss, Main.getInstance().getRow(), Main.getInstance().getLineStr().indexOf(badLinePart));
    }
    
    public CompileException(String mss, int row, int col) {
        super(mss + " in line " + row + (col < 0 ? "" : "\n" + Main.getInstance().getLineStr(row).replace('\t', ' ') + "\n" + Util.createArrow(' ', '^', col)));
        
        this.row = row;
        this.col = col;
        this.message = mss;
        Main.getInstance().putLineError(this);
    }
    
    public int getRow() {
        return row;
    }
    
    public String getTooltipMessage() {
        return message;
    }
}
