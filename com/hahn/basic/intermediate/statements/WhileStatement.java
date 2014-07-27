package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.parser.Node;

public abstract class WhileStatement extends Statement {
	Frame outerFrame, innerFrame;
	ExpressionStatement condition;
    
    public WhileStatement(Statement container, Node condition, Node body) {
        super(container);
        
        this.outerFrame = new Frame(getFrame(), null);
        this.innerFrame = new Frame(outerFrame, body);
        
        this.condition = outerFrame.handleExpression(condition);
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
