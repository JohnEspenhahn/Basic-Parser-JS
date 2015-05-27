package com.hahn.basic.lexer;

import java.util.List;

import com.hahn.basic.intermediate.CodeLines;

public interface ILexer {
    public void reset();
    
    public List<PackedToken> lex(CodeLines input);
}
