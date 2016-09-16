package com.hahn.basic.intermediate.statements;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.parser.Node;

public abstract class ForStatement extends Statement {
	private Frame outerFrame, modifyFrame, innerFrame;
	
    private DefineVarStatement define;
    private ExpressionStatement condition;
    
    /**
     * @param continer Owning statement
     * @param define EnumExpression.DEFINE
     * @param condition EnumExpression.EXPRESSION
     * @param modification List of EnumExpression.MODIFY
     * @param body EnumExpression.BLOCK
     */
    public ForStatement(Statement continer, Node define, Node condition, List<Node> modification, Node body) {
        super(continer);
        
        this.outerFrame  = new Frame(getFile(), getFrame(), null);
        this.modifyFrame = new Frame(getFile(), outerFrame, null);
        
        this.innerFrame  = new Frame(getFile(), outerFrame, body, true);
        
        this.define = outerFrame.defineVar(define);
        this.condition = outerFrame.handleExpression(condition);
        this.innerFrame.addTargetCode();
        
        for (Node modify: modification) {
            this.modifyFrame.addCode(modifyFrame.modifyVar(modify).getAsExp(modifyFrame));
        }
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
    
    public Frame getModifyFrame() {
        return modifyFrame;
    }
    
    public Frame getInnerFrame() {
    	return innerFrame;
    }
    
    public DefineVarStatement getDefineStatement() {
    	return define;
    }
    
    public ExpressionStatement getConditionStatement() {
    	return condition;
    }
    
    @Override
    public String toString() {
        return "for(" + getDefineStatement() + "; " + getConditionStatement() + "; " + getModifyFrame() + ") { " + getInnerFrame() + "}"; 
    }
}
