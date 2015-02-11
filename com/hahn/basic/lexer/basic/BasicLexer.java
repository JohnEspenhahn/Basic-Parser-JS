package com.hahn.basic.lexer.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
    private int row, index, matchStart, rowStart;
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
        this.index = 0;
        this.rowStart = 0;
        this.matchStart = 0;
        this.stream.clear();
    }
    
    @Override
    public List<PackedToken> lex(List<String> input) {
        stream.clear();
        
        String all = StringUtils.join(input, "");
        
        row = 1;
        index = 0;
        rowStart = 0;
        matchStart = 0;        
        while (index < all.length()) {
            char c = all.charAt(index);
            
            if (isWhitespace(c)) {
                index += 1;
                
                if (c == '\n') {
                    row += 1;
                    rowStart = index;
                }
                
                continue;
            } else if (isIdentifier(c)) {
                matchIdentifier(all);
            } else if (isNumeric(c)) {
                matchNumber(all);
            } else if (c == '.' && isNumeric(next(all))) {
                matchDouble(all, index);
            } else if (c == '\'') {
                matchChar(all);
            } else if (c == '"') {
                matchString(all);
            } else if (isOperator(c)) {
                matchOperator(all);
            } else if (isSeparator(c)) {
                matchSeparator(all);
            } else {
                throw new LexException(getRow(), getColumn());
            }
        }
        
        return stream;
    }
    
    private int getRow() {
        return row;
    }
    
    private int getColumn() {
        return getColumn(index);
    }
    
    private int getColumn(int idx) {
        return idx - rowStart;
    }
    
    private String getMatch(String line) {
        String match = line.substring(matchStart, index);
        matchStart = index;
        
        return match;
    }
    
    private void matchIdentifier(String line) {
        final int startIdx = index;
        
        do {
            index += 1;
        } while (index < line.length() && continuesIdentifier(line.charAt(index)));
        
        String match = getMatch(line);
        String identifier = line.substring(startIdx, index);
        for (EnumToken t: EnumToken.Group.identifiers) {
            if (t.getString().equals(identifier)) {
                stream.add(new PackedToken(t, match, getRow(), getColumn(startIdx)));
                return;
            }
        }
        
        stream.add(new PackedToken(EnumToken.IDENTIFIER, match, getRow(), getColumn(startIdx)));
    }
    
    private void matchNumber(String line) {
        matchNumber(line, index, false);
    }
    
    private void matchNumber(String line, final int startIdx, boolean hex) {
        char c = 0;
        do {
            index += 1;
        } while (index < line.length() && isNumeric(c = line.charAt(index)));
        
        if (c == '.') {
            if (!hex) matchDouble(line, startIdx);
            else throw new LexException(getRow(), getColumn());
        } else if (!hex && (c == 'x' || c == 'X')) {
            matchNumber(line, startIdx, true);
        } else {
            Enum<?> token = (hex ? EnumToken.HEX_INTEGER : EnumToken.INTEGER);
            
            String match = getMatch(line);
            stream.add(new PackedToken(token, match, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchDouble(String line, final int startIdx) {        
        char c = 0;
        do {
            index += 1;
        } while (index < line.length() && isNumeric(c = line.charAt(index)));
        
        if (c == '.') {
            throw new LexException(getRow(), getColumn());
        } else {
            String match = getMatch(line);
            stream.add(new PackedToken(EnumToken.FLOAT, match, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchChar(String line) {
        final int startIdx = index;
        index += 1;
        
        if (index < line.length() && line.charAt(index) == '\\') {
            index += 1;
        }
        
        index += 1;
        if (index >= line.length() || line.charAt(index) != '\'') {
            throw new LexException(getRow(), getColumn());
        } else {
            String match = getMatch(line);
            stream.add(new PackedToken(EnumToken.CHAR, match, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchString(String line) {
        final int startIdx = index;
        
        char c = 0;
        do {
            if (c == '\\') index += 2;
            else index += 1;
        } while (index < line.length() && (c = line.charAt(index)) != '"');
        
        if (c != '"') {
            throw new LexException(getRow(), getColumn());
        } else {
            index += 1;
            
            String match = getMatch(line);
            stream.add(new PackedToken(EnumToken.STRING, match, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchOperator(String line) {
        final int startIdx = index;
        
        // Get fullest operator possible
        do {
            index += 1;
        } while (index < line.length() && isOperator(line.charAt(index)));
        
        // Trim the operator till we find a valid match or trim everything
        do {
            String match = getMatch(line);
            String operator = line.substring(startIdx, index);
            for (EnumToken t: EnumToken.Group.operators) {
                if (t.getString().equals(operator)) {
                    stream.add(new PackedToken(t, match, getRow(), getColumn(startIdx)));
                    return;
                }
            }
            
            index -= 1;
        } while (index > startIdx);
        
        // No match
        throw new LexException(getRow(), getColumn(startIdx));
    }
    
    private void matchSeparator(String line) {
        final int startIdx = index;
        index += 1;

        String match = getMatch(line);
        String separator = line.substring(startIdx, index);
        for (EnumToken t: EnumToken.Group.separators) {
            if (t.getString().equals(separator)) {
                stream.add(new PackedToken(t, match, getRow(), getColumn(startIdx)));
                return;
            }
        }
        
        // No match
        throw new LexException(getRow(), getColumn(startIdx));
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
        if (index + 1 < str.length()) return str.charAt(index + 1);
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
    static final char[] operators = "#+-/*%=<>!^&|?:.".toCharArray();
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
