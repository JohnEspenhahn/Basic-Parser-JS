package com.hahn.basic.parser;

import java.security.InvalidParameterException;

import com.hahn.basic.lexer.IEnumToken;
import com.hahn.basic.lexer.PackedToken;

abstract class IParser {
    private Class<? extends IEnumToken> EnumTokens;
    private Class<? extends IEnumExpression> EnumExpressions;
    
    public IParser(Class<? extends IEnumToken> enumTokens, Class<? extends IEnumExpression> enumExpressions) {
        if (!enumExpressions.isEnum()) throw new InvalidParameterException("Expressions class must be an Enum");
        else if (!enumTokens.isEnum()) throw new InvalidParameterException("Tokens class must be an Enum");
        
        this.EnumTokens = enumTokens;
        this.EnumExpressions = enumExpressions;
    }
    
    public Class<? extends IEnumToken> getTokenClass() {
        return EnumTokens;
    }
    
    public Class<? extends IEnumExpression> getExpressionClass() {
        return EnumExpressions;
    }
    
    public abstract PackedToken[] getStream();
    public abstract int getStreamIdx();
    public abstract void addToStreamIdx(int amnt);
    
    public abstract void setFurthest(int amnt);
    public abstract int getFurthest();
    
    public abstract Node getNode();
}
