package com.hahn.basic.lexer.basic;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.lexer.ILexer;
import com.hahn.basic.lexer.ILexerFactory;

public class BasicLexerFactory implements ILexerFactory {

    @Override
    public ILexer newInstance(CodeFile file) {
        return new BasicLexer(file);
    }
    
}
