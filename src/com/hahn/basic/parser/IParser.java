package com.hahn.basic.parser;

import java.security.InvalidParameterException;

import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.lexer.regex.IEnumRegexToken;

abstract class IParser {
    private Class<? extends IEnumRegexToken> EnumTokens;
    private Class<? extends IEnumExpression> EnumExpressions;
    
    public IParser(Class<? extends IEnumRegexToken> enumTokens, Class<? extends IEnumExpression> enumExpressions) {
        if (!enumExpressions.isEnum()) throw new InvalidParameterException("Expressions class must be an Enum");
        else if (!enumTokens.isEnum()) throw new InvalidParameterException("Tokens class must be an Enum");
        
        this.EnumTokens = enumTokens;
        this.EnumExpressions = enumExpressions;
    }
    
    public final Class<? extends IEnumRegexToken> getTokenClass() {
        return EnumTokens;
    }
    
    public final Class<? extends IEnumExpression> getExpressionClass() {
        return EnumExpressions;
    }
    
    /**
     * Get the input stream
     * @return The input stream
     */
    public abstract PackedToken[] getStream();
    
    /** 
     * Get the current index being looked at in the input stream
     * @return The current index looking at in the stream 
     */
    public abstract int getStreamIdx();
    
    /**
     * Shift the current index being looked at in the input stream
     * @param amnt The amount to add to the stream index
     */
    public abstract void addToStreamIdx(int amnt);
    
    /**
     * Keep track of the farthest distance advanced in the stream
     * @param amnt The farthest distance advanced in the stream
     */
    public abstract void setFarthest(int amnt);
    
    /**
     * Get the farthest distance advanced in the stream
     * @return The farthest distance advanced in the stream
     */
    public abstract int getFarthest();
    
    /**
     * Get the main node of this parser
     * @return The main node of this parser
     */
    public abstract Node getNode();
}
