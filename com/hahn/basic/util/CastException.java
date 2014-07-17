package com.hahn.basic.util;

public class CastException extends CompileException {
    private static final long serialVersionUID = 833355693096718661L;

    public CastException(String mss) {
        super(mss);
    }

    public CastException(String mss, CompileException e) {  
        super(mss + e.getMessage(), false);
    }

}
