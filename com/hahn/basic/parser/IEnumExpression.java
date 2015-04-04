package com.hahn.basic.parser;

public interface IEnumExpression extends IEnumToken {
    public void setSubExpressions(Enum<?>[][] se);
    public Enum<?>[][] getSubExpressions();    
    public String getBNFString();    
    public boolean canFlatten();
}
