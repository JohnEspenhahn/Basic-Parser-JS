package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;

public abstract class ExpressionStatementObject extends BasicObject {
    private ExpressionStatement statement;
    
    public ExpressionStatementObject(ExpressionStatement s) {
        super("[{...}]", Type.UNDEFINED);
        
        this.statement = s;
    }
    
    @Override
    public Type getType() {
        return statement.getObj().getType();
    }
    
    @Override
    public void setType(Type t) {
        throw new RuntimeException("Cannot set the type of " + getClass());
    }
    
    @Override
    public BasicObject castTo(Type type) {
        statement.castTo(type);
        
        return this;
    }
}
