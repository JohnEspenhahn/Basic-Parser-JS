package com.hahn.basic.lexer;


public class PackedToken {
    public final Enum<?> token;
    public final String value, fullText;
    
    public final int row, col;

    public PackedToken(Enum<?> token, String val, String fullText, int row, int col) {
        this.token = token;
        this.value = val;
        this.fullText = fullText;
        
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return token + "(" + value + ")";
    }
}
