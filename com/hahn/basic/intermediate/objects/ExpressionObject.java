package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;

public abstract class ExpressionObject extends BasicObject {
    
    @Delegate(types = BasicObject.class, excludes=ExpressionObject.ExcludesList.class)
    private BasicObject heldObj;
    
    private ExpressionStatement statement;
    
    public ExpressionObject(ExpressionStatement s) {
        super(s.getObj().getName(), Type.UNDEFINED);
        
        this.heldObj = s.getObj();
        
        this.statement = s;
    }
    
    public ExpressionStatement getStatement() {
        return statement;
    }
    
    public BasicObject getHeldObject() {
        return heldObj;
    }
    
    @Override
    public boolean isGrouped() {
        return super.isGrouped() || getStatement().isGrouped();
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        statement.reverseOptimize();
        
        return super.isLastUse(this);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        statement.forwardOptimize();
    }
    
    @Override
    public abstract String toTarget();
    
    interface ExcludesList {
        public BasicObject castTo(Type t);
        public ExpressionStatement getAsExp(Statement container);
        
        // Explicitly implemented functions
        public boolean isGrouped();
        public boolean setInUse(IIntermediate by);
        public void takeRegister(IIntermediate by);
        public String toTarget();
    }
}
