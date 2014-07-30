package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class ExpressionStatement extends Statement {
    private BasicObject obj;
    
    private boolean gotAsObject;
    
    public ExpressionStatement(Statement continer, BasicObject obj) {
        super(continer);
        
        this.obj = obj;
        this.gotAsObject = false;
    }
    
    public BasicObject getObj() {
        return obj;
    }
    
    public void setObj(BasicObject obj) {
        enforce();
        
        this.obj = obj;
    }
    
    public void setObj(ExpressionStatement otherExp) {
        enforce();
        
        this.obj = otherExp.obj;
    }
    
    public void castTo(Type type) {
        enforce();
        
        this.obj = this.obj.castTo(type);
    }
    
    @Override
    public boolean reverseOptimize() {
        Main.setLine(row);
        
        getObj().setInUse(this);
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        Main.setLine(row);
        
        getObj().takeRegister(this);
        
        return false;
    }
    
    /**
     * Get an object version of this statement. Should
     * only be called once the statement is fully 
     * evaluated
     * @return ExpressionStatementObject
     */
    public BasicObject getAsExpObj() {
        this.gotAsObject = true;
        
        return LangCompiler.factory.ExpressionObject(this);
    }
    
    /**
     * Ensure valid state
     */
    public void enforce() {
        if (gotAsObject) {
            throw new RuntimeException("Tried to modify ExpressionStatement after getting as ExpressionObject!");
        }
    }
    
    @Override
    public String toString() {
        return getObj().toString();
    }
}
