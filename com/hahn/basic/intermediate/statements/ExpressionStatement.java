package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class ExpressionStatement extends Statement {
    private BasicObject obj;
    
    public ExpressionStatement(Statement continer, BasicObject obj) {
        super(continer);
        
        this.obj = obj;
    }
    
    public void setObj(BasicObject obj) {
        this.obj = obj;
    }
    
    public void setObj(ExpressionStatement otherExp) {
        this.obj = otherExp.obj;
    }
    
    public BasicObject getObj() {
        return obj;
    }
    
    public void castTo(Type type) {
        obj = obj.castTo(type);
    }
    
    public BasicObject getAsExpObj() {
        return LangCompiler.factory.ExpressionStatementObject(this);
    }
    
    @Override
    public String toString() {
        return "{...}" + getObj();
    }
}
