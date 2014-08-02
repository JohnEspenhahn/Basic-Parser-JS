package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class TernaryObject extends BasicObject {
    private Frame then_frame, else_frame;
    private BasicObject conditional;
    private ExpressionStatement exp_then, exp_else;
    
    public TernaryObject(Statement container, BasicObject condition, Node node_then, Node node_else) {
        super("(A?B:C)", Type.UNDEFINED);
        
        this.conditional = condition;
        
        this.then_frame = new Frame(container.getFrame(), null);
        this.exp_then = this.then_frame.handleExpression(node_then);
        
        this.else_frame = new Frame(container.getFrame(), null);
        this.exp_else = this.else_frame.handleExpression(node_else);
    }
    
    public BasicObject getConditional() {
        return conditional;
    }
    
    public ExpressionStatement getThenExpression() {
        return exp_then;
    }
    
    public ExpressionStatement getElseExpression() {
        return exp_else;
    }
    
    @Override
    public boolean isTernary() {
        return true;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {        
        getElseExpression().reverseOptimize();
        getThenExpression().reverseOptimize();
        
        getConditional().setInUse(by);
        
        Type typeThen = getThenExpression().getObj().getType();
        Type typeElse = getElseExpression().getObj().getType();
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
        
        getThenExpression().reverseOptimize();
        getElseExpression().reverseOptimize();
    }
    
    @Override
    public String toString() {
        return String.format("%s ? %s : %s", getConditional(), getThenExpression(), getElseExpression());
    }
}
