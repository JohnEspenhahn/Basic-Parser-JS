package com.hahn.basic.lexer;

import com.hahn.basic.intermediate.CodeFile;

public interface ILexerFactory {
    ILexer newInstance(CodeFile file);
}
