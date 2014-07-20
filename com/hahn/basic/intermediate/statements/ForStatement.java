package com.hahn.basic.intermediate.statements;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.parser.Node;

public abstract class ForStatement extends Statement {
	private Frame outerFrame, innerFrame;
	
    private Node define;
    private Node condition;
    private List<Node> modification;
    
    /**
     * @param continer Owning statement
     * @param define EnumExpression.DEFINE
     * @param condition EnumExpression.EXPRESSION
     * @param modification List of EnumExpression.MODIFY
     * @param body EnumExpression.BLOCK
     */
    public ForStatement(Statement continer, Node define, Node condition, List<Node> modification, Node body) {
        super(continer);
        
        this.outerFrame = new Frame(getFrame(), null);
        this.innerFrame = new Frame(outerFrame, body);
        
        this.define = define;
        this.condition = condition;
        this.modification = modification;
    }

    public Frame getOuterFrame() {
    	return outerFrame;
    }
    
    public Frame getInnerFrame() {
    	return innerFrame;
    }
    
    public Node getDefineNode() {
    	return define;
    }
    
    public Node getConditionNode() {
    	return condition;
    }
    
    public List<Node> getModifyNodes() {
    	return modification;
    }
    
    @Override
    public String toString() {
        return "for(" + define + "; " + condition + "; " + modification + ") { " + innerFrame + "}"; 
    }
}
