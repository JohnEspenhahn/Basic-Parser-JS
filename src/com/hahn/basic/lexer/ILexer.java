package com.hahn.basic.lexer;

import java.util.List;

import com.hahn.basic.util.exceptions.LexException;

public interface ILexer {    
    public List<PackedToken> lex() throws LexException;
}
