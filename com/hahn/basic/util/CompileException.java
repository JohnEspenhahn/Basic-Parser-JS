package com.hahn.basic.util;

import com.hahn.basic.Main;

public class CompileException extends RuntimeException {
    private static final long serialVersionUID = 4974149633641094015L;
    
    public CompileException(String mss, boolean addLine) {
        super(mss + (addLine ? " in line " + Main.getRow() : ""));
    }
    
    public CompileException(String mss, int col) {
        this(mss + " in line " + Main.getRow() + (col < 0 ? "" : "\n" + Main.getLineStr() + "\n" + Util.createArrow(' ', '^', col)), false);
    }
    
    public CompileException(String mss, String badLinePart) {
        this(mss, Main.getLineStr().indexOf(badLinePart));
    }
    
    public CompileException(String mss) {
        this(mss, true);
    }
}
