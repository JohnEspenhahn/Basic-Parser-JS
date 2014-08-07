package com.hahn.basic.lexer.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.lexer.ILexer;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.util.exceptions.LexException;

/**
 * A custom pattern matching lexer. More basic, but also more
 * efficient, than standard java regular expressions.
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
        
    }
    
    @Override
    public List<PackedToken> lex(List<String> input) {
        stream.clear();
        
        Iterator<String> it = input.iterator();
        for (row = 1; it.hasNext(); row++) {
            String line = it.next();
            
            for (column = 0; column < line.length();) {
                char c = line.charAt(column++);
                
                if (isWhitespace(c)) {
                    continue;
                } else if (isIdentifier(c)) {
                    matchIdentifier(line);
                } else if (isNumeric(c)) {
                    matchNumber(line);
                } else if (c == '.' && isNumeric(next(line))) {
                    matchDouble(line, column, 1);
                } else {
                    matchOperator(line);
                }
            }
        }
        
        return stream;
    }
    
    private void matchIdentifier(String line) {
        final int start = column;
        int length = 1;
        while (continuesIdentifier(line.charAt(++column))) {
            length += 1;
        }
        
        String identifier = line.substring(start, start + length);
        for (EnumBasicToken t: EnumBasicToken.identifiers) {
            if (t.getString().equals(identifier)) {
                stream.add(new PackedToken(t, identifier, row, column));
                return;
            }
        }
        
        stream.add(new PackedToken(EnumBasicToken.IDENTIFIER, identifier, row, start));
    }
    
    private void matchNumber(String line) {
        matchNumber(line, column, 1, false);
    }
    
    private void matchNumber(String line, final int start, int length, boolean hex) {        
        char c;
        while (isNumeric(c = line.charAt(++column))) {
            length += 1;
        }
        
        if (c == '.') {
            if (!hex) matchDouble(line, start, length);
            else throw new LexException(row, start + length);
        } else if (!hex && (c == 'x' || c == 'X')) {
            matchNumber(line, start, length, true);
        } else {
            Enum<?> token = (hex ? EnumBasicToken.HEX_INTEGER : EnumBasicToken.INTEGER);
            String identifier = line.substring(start, start + length);
            stream.add(new PackedToken(token, identifier, row, start));
        }
    }
    
    private void matchDouble(String line, final int start, int length) {
        char c;
        while (isNumeric(c = line.charAt(++column))) {
            length += 1;
        }
        
        if (c == '.') {
            throw new LexException(row, start + length);
        } else {
            String identifier = line.substring(start, start + length);
            stream.add(new PackedToken(EnumBasicToken.FLOAT, identifier, row, start));
        }
    }
    
    private void matchOperator(String line) {
        char c = line.charAt(column);
        // TODO match operator
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
    
    private static boolean search(char[] arr, char c) {
        for (char ac: arr) {
            if (ac == c) return true;
        }
        
        return false;
    }
    
    static final char[] identifier_start = "_$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    static final char[] identifier_continue = "_$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    static final char[] whitespace = " \t\n\f\r".toCharArray();
    static final char[] numeric    = "0123456789".toCharArray();
    static {
        Arrays.sort(identifier_start);
        Arrays.sort(identifier_continue);
    }
}
