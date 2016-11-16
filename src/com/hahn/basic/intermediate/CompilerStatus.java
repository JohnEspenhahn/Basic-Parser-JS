package com.hahn.basic.intermediate;

import com.hahn.basic.util.exceptions.CompileException;

public class CompilerStatus {
    private boolean debug = false, pretty = false;
    
    public void toggleDebug() {
        debug = !debug;
        System.out.println("Debug = " + debug);
        System.out.println();
    }
    
    public boolean isDebugging() {
        return debug;
    }
    
    public void togglePretty() {
        pretty = !pretty;
        System.out.println("Pretty = " + pretty);
        System.out.println();
    }
    
    public boolean isPretty() {
        return pretty;
    }
    
    public void printCompileException(CompileException e) {
        if (debug) e.printStackTrace();
        else System.out.println("ERROR: " + e.getMessage());
    }
}
