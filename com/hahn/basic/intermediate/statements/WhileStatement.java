package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.ExpressionObject;
import com.hahn.basic.parser.Node;

public abstract class WhileStatement extends Statement {
	Frame outerFrame, innerFrame;
	ExpressionObject condition;
    
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
    
    public ExpressionObject getConditionObject() {
    	return condition;
    }
    
    @Override
    public String toString() {
        return String.format("while (%s) { %s }", getConditionObject(), getInnerFrame());
    }
}
