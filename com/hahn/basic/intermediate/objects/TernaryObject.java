package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class TernaryObject extends BasicObject {
    private BasicObject conditional, then_obj, else_obj;
    
    public TernaryObject(Statement container, BasicObject condition, Node node_then, Node node_else) {
        super("?:", Type.UNDEFINED);
        
        this.conditional = condition;
        
        this.then_obj = container.getFrame().handleExpression(node_then).getObj();
        this.else_obj = container.getFrame().handleExpression(node_else).getObj();
    }
    
    public BasicObject getConditional() {
        return conditional;
    }
    
    public BasicObject getThen() {
        return then_obj;
    }
    
    public BasicObject getElse() {
        return else_obj;
    }
    
    protected boolean doGroup(BasicObject obj) {
        return obj.isGrouped() || obj.isTernary();
    }
    
    @Override
    public boolean isTernary() {
        return true;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {        
        getElse().setInUse(by);
        getThen().setInUse(by);
        
        getConditional().setInUse(by);
        
        // Type check
        Type typeThen = getThen().getType();
        Type typeElse = getElse().getType();
        Type overruling = Type.safeCombine(typeThen, typeElse);
        if (overruling != null) {
            setType(overruling);
        } else {
            throw new CompileException("Invalid inline if, types don't match (" + typeThen + " : " + typeElse + ")");
        }
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        getConditional().takeRegister(by);
        
        getThen().takeRegister(by);
        getElse().takeRegister(by);
    }
    
    @Override
    public String toString() {
        return String.format("%s ? %s : %s", getConditional(), getThen(), getElse());
    }
}
