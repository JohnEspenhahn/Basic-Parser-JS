package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

public abstract class TernaryObject extends BasicObject {
    private IBasicObject conditional, then_obj, else_obj;
    
    private CodeFile file;
    private int row, col;
    
    public TernaryObject(Statement container, IBasicObject condition, Node node_then, Node node_else, CodeFile file, int row, int col) {
        super("?:", Type.UNDEFINED);
        
        this.conditional = condition;
        
        this.then_obj = container.getFrame().handleNextExpressionChildObject(node_then, null);
        this.else_obj = container.getFrame().handleNextExpressionChildObject(node_else, null);
        
        this.file = file;
        this.row = row;
        this.col = col;
    }
    
    public IBasicObject getConditional() {
        return conditional;
    }
    
    public IBasicObject getThen() {
        return then_obj;
    }
    
    public IBasicObject getElse() {
        return else_obj;
    }
    
    protected boolean doGroup(IBasicObject obj) {
        return obj.isGrouped() || obj.isTernary();
    }
    
    @Override
    public boolean isTernary() {
        return true;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {        
        getElse().setInUse(this);
        getThen().setInUse(this);
        
        getConditional().setInUse(this);
        
        // Type pair for then and else
        setType(Type.merge(getThen().getType(), getElse().getType(), file, row, col, true));
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        getConditional().takeRegister(this);
        
        getThen().takeRegister(this);
        getElse().takeRegister(this);
    }
    
    @Override
    public String toString() {
        return String.format("%s ? %s : %s", getConditional(), getThen(), getElse());
    }
}
