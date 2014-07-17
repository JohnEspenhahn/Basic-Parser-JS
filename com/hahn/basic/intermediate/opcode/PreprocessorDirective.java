package com.hahn.basic.intermediate.opcode;


public enum PreprocessorDirective {
    IMPORT, ORIGIN, FILL, CONST;
    
    public String getName() {
        return name();
    }
}
