package com.hahn.basic.lexer.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.lexer.ILexer;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.util.exceptions.LexException;

/**
 * A lexer that is harder to customize than the RegexLexer,
 * but is more efficient. Identifiers and operators can
 * be changed in com.hahn.basic.definition.EnumToken to be
 * changed here, but a custom class can not be used. Literals
 * and variable name identifiers can not be customized with
 * this lexer.
 * 
 * @author John Espenhahn
 * 
 */
public class BasicLexer implements ILexer {    
    private int row, column;
    private final List<PackedToken> stream;
    
    /**
     * Create a new lexer with static definition for the BASIC language
     */
    public BasicLexer() {
        stream = new ArrayList<PackedToken>();
    }
    
    @Override
    public void reset() {
        this.row = 0;
        this.column = 0;
        this.stream.clear();
    }
    
    @Override
    public List<PackedToken> lex(List<String> input) {
        stream.clear();
        
        Iterator<String> it = input.iterator();
        for (row = 1; it.hasNext(); row++) {
            String line = it.next();
            
            for (column = 0; column < line.length();) {
                char c = line.charAt(column);
                
                if (isWhitespace(c)) {
                    column += 1;
                    continue;
                } else if (isIdentifier(c)) {
                    matchIdentifier(line);
                } else if (isNumeric(c)) {
                    matchNumber(line);
                } else if (c == '.' && isNumeric(next(line))) {
                    matchDouble(line, column);
                } else if (c == '\'') {
                    matchChar(line);
                } else if (c == '"') {
                    matchString(line);
                } else if (isOperator(c)) {
                    matchOperator(line);
                } else if (isSeparator(c)) {
                    matchSeparator(line);
                } else {
                    throw new LexException(row, column);
                }
            }
        }
        
        return stream;
    }
    
    private void matchIdentifier(String line) {
        final int start = column;
        
        do {
            column += 1;
        } while (column < line.length() && continuesIdentifier(line.charAt(column)));
        
        String identifier = line.substring(start, column);
        for (EnumToken t: EnumToken.Group.identifiers) {
            if (t.getString().equals(identifier)) {
                stream.add(new PackedToken(t, identifier, row, start));
                return;
            }
        }
        
        stream.add(new PackedToken(EnumToken.IDENTIFIER, identifier, row, start));
    }
    
    private void matchNumber(String line) {
        matchNumber(line, column, false);
    }
    
    private void matchNumber(String line, final int start, boolean hex) {
        char c = 0;
        do {
            column += 1;
        } while (column < line.length() && isNumeric(c = line.charAt(column)));
        
        if (c == '.') {
            if (!hex) matchDouble(line, start);
            else throw new LexException(row, column);
        } else if (!hex && (c == 'x' || c == 'X')) {
            matchNumber(line, start, true);
        } else {
            Enum<?> token = (hex ? EnumToken.HEX_INTEGER : EnumToken.INTEGER);
            String identifier = line.substring(start, column);
            stream.add(new PackedToken(token, identifier, row, start));
        }
    }
    
    private void matchDouble(String line, final int start) {        
        char c = 0;
        do {
            column += 1;
        } while (column < line.length() && isNumeric(c = line.charAt(column)));
        
        if (c == '.') {
            throw new LexException(row, column);
        } else {
            String identifier = line.substring(start, column);
            stream.add(new PackedToken(EnumToken.FLOAT, identifier, row, start));
        }
    }
    
    private void matchChar(String line) {
        final int start = column;
        column += 1;
        
        if (column < line.length() && line.charAt(column) == '\\') {
            column += 1;
        }
        
        column += 1;
        if (column >= line.length() || line.charAt(column) != '\'') {
            throw new LexException(row, column);
        } else {
            String identifier = line.substring(start, column);
            stream.add(new PackedToken(EnumToken.CHAR, identifier, row, start));
        }
    }
    
    private void matchString(String line) {
        final int start = column;
        
        char c = 0;
        do {
            if (c == '\\') column += 2;
            else column += 1;
        } while (column < line.length() && (c = line.charAt(column)) != '"');
        
        if (c != '"') {
            throw new LexException(row, column);
        } else {
            String identifier = line.substring(start, column);
            stream.add(new PackedToken(EnumToken.STRING, identifier, row, start));
        }
    }
    
    private void matchOperator(String line) {
        final int start = column;
        
        // Get fullest operator possible
        do {
            column += 1;
        } while (column < line.length() && isOperator(line.charAt(column)));
        
        // Trim the operator till we find a valid match or trim everything
        do {
            String operator = line.substring(start, column);
            for (EnumToken t: EnumToken.Group.operators) {
                if (t.getString().equals(operator)) {
                    stream.add(new PackedToken(t, operator, row, start));
                    return;
                }
            }
            
            column -= 1;
        } while (column > start);
        
        // No match
        throw new LexException(row, start);
    }
    
    private void matchSeparator(String line) {
        final int start = column;
        column += 1;
        
        String separator = line.substring(start, column);
        for (EnumToken t: EnumToken.Group.separators) {
            if (t.getString().equals(separator)) {
                stream.add(new PackedToken(t, separator, row, start));
                return;
            }
        }
        
        // No match
        throw new LexException(row, start);
    }
    
    /**
     * Gets the first character in the string past `this.column`
     * (without changing `this.column`) or returns \0 if reached
     * the end of the string
     * 
     * @param str The string to check
     * @return The next character
     */
    private char next(String str) {
        if (column + 1 < str.length()) return str.charAt(column + 1);
        else return 0;
    }
    
    private static boolean isWhitespace(char c) {
        return search(whitespace, c);
    }
    
    private static boolean isNumeric(char c) {
        return search(numeric, c);
    }
    
    private static boolean isIdentifier(char c) {
        return Arrays.binarySearch(identifier_start, c) >= 0;
    }
    
    private static boolean continuesIdentifier(char c) {
        return Arrays.binarySearch(identifier_continue, c) >= 0;
    }
    
    private static boolean isOperator(char c) {
        return Arrays.binarySearch(operators, c) >= 0;
    }
    
    private static boolean isSeparator(char c) {
        return search(separators, c);
    }
    
    private static boolean search(char[] arr, char c) {
        for (char ac: arr) {
            if (ac == c) return true;
        }
        
        return false;
    }
    
    static final char[] identifier_start = "_$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    static final char[] identifier_continue = "_$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    static final char[] operators = "+-/*%=<>!^&|?:.".toCharArray();
    static final char[] separators = ",{}[]();".toCharArray();
    static final char[] whitespace = " \t\n\f\r".toCharArray();
    static final char[] numeric    = "0123456789".toCharArray();
    static {
        Arrays.sort(identifier_start);
        Arrays.sort(identifier_continue);
        Arrays.sort(operators);
        Arrays.sort(separators);
        Arrays.sort(whitespace);
        Arrays.sort(numeric);
    }
}
