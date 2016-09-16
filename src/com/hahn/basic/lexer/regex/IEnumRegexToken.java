package com.hahn.basic.lexer.regex;

import com.hahn.basic.parser.IEnumToken;

/**
 * The regex lexer tokens enum must implement this
 * 
 * @author John Espenhahn
 *
 */
public interface IEnumRegexToken extends IEnumToken {
    public String getRegex();
}
