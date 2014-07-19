package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.parser.Node;

public abstract class WhileStatement extends Statement {
	Frame outerFrame, innerFrame;
    Node condition;
    
    public WhileStatement(Statement container, Node condition, Node body) {
        super(container);
        
        this.outerFrame = new Frame(getFrame(), null);
        this.innerFrame = new Frame(outerFrame, body);
        
        this.condition = condition;
    }

    public Frame getOuterFrame() {
    	return outerFrame;
    }
    
    public Frame getInnerFrame() {
    	return innerFrame;
    }
    
    public Node getConditionNode() {
    	return condition;
    }
}
