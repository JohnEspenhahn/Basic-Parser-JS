package com.hahn.basic.lexer;


public class PackedToken {
    public final IEnumToken token;
    public final String value;
    
    public final int row, col;

    public PackedToken(IEnumToken token, String val, int row, int col) {
        this.token = token;
        this.value = val;
        
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return token + "(" + value + ")";
    }
}
