package com.hahn.basic.lexer;

import java.util.List;

public interface ILexer {
    public void reset();
    
    public List<PackedToken> lex(List<String> input);
}
