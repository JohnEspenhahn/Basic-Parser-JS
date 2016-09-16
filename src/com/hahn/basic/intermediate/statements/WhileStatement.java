package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.parser.Node;

public abstract class WhileStatement extends Statement {
	Frame outerFrame, innerFrame;
	ExpressionStatement condition;
    
    public WhileStatement(Statement container, Node condition, Node body) {
        super(container);
        
        this.outerFrame = new Frame(getFile(), getFrame(), null);
        
        this.innerFrame = new Frame(getFile(), outerFrame, body, true);
        this.innerFrame.addTargetCode();
        
        this.condition = outerFrame.handleExpression(condition);
    }

    @Override
    public boolean isBlock() {
        return true;
    }
    
    @Override
    public boolean hasReturn() {
        return getInnerFrame().hasReturn();
    }
    
    public Frame getOuterFrame() {
    	return outerFrame;
    }
    
    public Frame getInnerFrame() {
    	return innerFrame;
    }
    
    public ExpressionStatement getConditionStatement() {
    	return condition;
    }
    
    @Override
    public String toString() {
        return String.format("while (%s) { %s }", getConditionStatement(), getInnerFrame());
    }
}
