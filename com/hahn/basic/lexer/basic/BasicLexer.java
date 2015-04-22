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
    
    private String line;
    
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
        
        line = StringUtils.join(input, "");
        
        row = 1;
        index = 0;
        rowStart = 0;
        matchStart = 0;        
        while (index < line.length()) {
            char c = getChar();
            
            if (c == '/' && next() == '/') {
                handleComment();
            } else if (isWhitespace(c)) {
                handleWhitespace(c);
            } else if (isIdentifier(c)) {
                matchIdentifier();
            } else if (isNumeric(c)) {
                matchNumber();
            } else if (c == '.' && isNumeric(next())) {
                matchDouble(index);
            } else if (c == '\'') {
                matchChar();
            } else if (c == '"') {
                matchString();
            } else if (isOperator(c)) {
                matchOperator();
            } else if (isSeparator(c)) {
                matchSeparator();
            } else {
                matchOther();
            }
        }
        
        return stream;
    }
    
    private char getChar() {
        return line.charAt(index);
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
    
    private String getMatch() {
        String match = substring(matchStart);
        matchStart = index;
        
        return match;
    }
    
    /**
     * Get a substring of the line
     * @param start The starting index
     * @return line.substring(start, index)
     */
    private String substring(int start) {
        return line.substring(start, index);
    }
    
    private void handleComment() {
        final int startIdx = index;
        
        index += 1;        
        char c = getChar();
        if (c != '/') throw new LexException(getRow(), getColumn());
        
        while (c != '\n') {
            index += 1;
            c = getChar();
        }
        
        String match = getMatch();
        stream.add(new PackedToken(EnumToken.COMMENT, match, startIdx, getRow(), getColumn(startIdx)));
    }
    
    private void handleWhitespace(char c) {
        index += 1;
        
        if (c == '\n') {
            row += 1;
            rowStart = index;
        }
    }
    
    private void matchIdentifier() {
        final int startIdx = index;
        
        do {
            index += 1;
        } while (index < line.length() && continuesIdentifier(getChar()));
        
        String match = getMatch();
        String identifier = substring(startIdx);
        for (EnumToken t: EnumToken.Group.identifiers) {
            if (t.getString().equals(identifier)) {
                stream.add(new PackedToken(t, match, startIdx, getRow(), getColumn(startIdx)));
                return;
            }
        }
        
        stream.add(new PackedToken(EnumToken.IDENTIFIER, match, startIdx, getRow(), getColumn(startIdx)));
    }
    
    private void matchNumber() {
        matchNumber(index, false);
    }
    
    private void matchNumber(final int startIdx, boolean hex) {        
        char c = 0;
        do {
            index += 1;
        } while (index < line.length() && isNumeric(c = getChar()));
        
        if (c == '.') {
            if (!hex) matchDouble(startIdx);
            else throw new LexException(getRow(), getColumn());
        } else if (!hex && (c == 'x' || c == 'X')) {
            matchNumber(startIdx, true);
        } else {
            Enum<?> token = (hex ? EnumToken.HEX_INT : EnumToken.REAL);
            
            String match = getMatch();
            stream.add(new PackedToken(token, match, startIdx, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchDouble(final int startIdx) {        
        char c = 0;
        do {
            index += 1;
        } while (index < line.length() && isNumeric(c = getChar()));
        
        if (c == '.') {
            throw new LexException(getRow(), getColumn());
        } else {
            String match = getMatch();
            stream.add(new PackedToken(EnumToken.REAL, match, startIdx, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchChar() {
        final int startIdx = index;
        index += 1;
        
        if (index < line.length() && getChar() == '\\') {
            index += 1;
        }
        
        index += 1;
        if (index >= line.length() || getChar() != '\'') {
            throw new LexException(getRow(), getColumn());
        } else {
            String match = getMatch();
            stream.add(new PackedToken(EnumToken.CHAR, match, startIdx, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchString() {
        final int startIdx = index;
        
        char c = 0;
        do {
            if (c == '\\') index += 2;
            else index += 1;
        } while (index < line.length() && (c = getChar()) != '"');
        
        if (c != '"') {
            throw new LexException(getRow(), getColumn());
        } else {
            index += 1;
            
            String match = getMatch();
            stream.add(new PackedToken(EnumToken.STRING, match, startIdx, getRow(), getColumn(startIdx)));
        }
    }
    
    private void matchOperator() {
        final int startIdx = index;
        
        // Get fullest operator possible
        do {
            index += 1;
        } while (index < line.length() && isOperator(getChar()));
        
        // Trim the operator till we find a valid match or trim everything
        do {
            String operator = substring(startIdx);
            for (EnumToken t: EnumToken.Group.operators) {
                if (t.getString().equals(operator)) {
                    stream.add(new PackedToken(t, getMatch(), startIdx, getRow(), getColumn(startIdx)));
                    return;
                }
            }
            
            index -= 1;
        } while (index > startIdx);
        
        // No match
        throw new LexException(getRow(), getColumn(startIdx));
    }
    
    private void matchSeparator() {
        final int startIdx = index;
        index += 1;

        String separator = substring(startIdx);
        for (EnumToken t: EnumToken.Group.separators) {
            if (t.getString().equals(separator)) {
                stream.add(new PackedToken(t, getMatch(), startIdx, getRow(), getColumn(startIdx)));
                return;
            }
        }
        
        // No match
        throw new LexException(getRow(), getColumn(startIdx));
    }
    
    private void matchOther() {
        final int startIdx = index;
        index += 1;

        String match = getMatch();
        stream.add(new PackedToken(EnumToken.OTHER, match, startIdx, getRow(), getColumn(startIdx)));
    }
    
    /**
     * Gets the first character in the string past `this.column`
     * (without changing `this.column`) or returns \0 if reached
     * the end of the string
     * 
     * @return The next character in `line`
     */
    private char next() {
        if (index + 1 < line.length()) return line.charAt(index + 1);
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
