package com.hahn.basic.intermediate.statements;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.IntermediateList;

public abstract class ForStatement extends Statement {
	private Frame outerFrame, innerFrame;
	
    private DefineVarStatement define;
    private ExpressionStatement condition;
    private IntermediateList<OPObject> modification;
    
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
        
        this.define = outerFrame.defineVar(define);
        this.condition = outerFrame.handleExpression(condition);
        
        this.modification = new IntermediateList<OPObject>();
        for (Node modify: modification) {
            this.modification.add(outerFrame.modifyVar(modify));
        }
    }

    public Frame getOuterFrame() {
    	return outerFrame;
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
    
    public IntermediateList<OPObject> getModifyStatements() {
    	return modification;
    }
    
    @Override
    public String toString() {
        return "for(" + define + "; " + condition + "; " + modification + ") { " + innerFrame + "}"; 
    }
}
