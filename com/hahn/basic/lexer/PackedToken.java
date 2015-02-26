package com.hahn.basic.lexer;


public class PackedToken {
    public final Enum<?> token;
    public final String value;
    
    public final int idx, row, col;

    public PackedToken(Enum<?> token, String val, int idx, int row, int col) {
        this.token = token;
        this.value = val;
        
        this.idx = idx;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return token + "(" + value + ")";
    }
}
