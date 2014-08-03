package com.hahn.basic.intermediate.statements;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.ExpressionObject;
import com.hahn.basic.parser.Node;

public abstract class IfStatement extends Statement {
    private List<Conditional> conditionals;
    
    public IfStatement(Statement container, List<Conditional> elseStatements) {
        super(container);
        
        this.conditionals = elseStatements;
    }

    public List<Conditional> getConditionals() {
    	return conditionals;
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        
        boolean first = true;
        for (Conditional cnd: conditionals) {
            if (!first) str.append("else ");
            else first = false;
            
            if (cnd.hasCondition()) {
                str.append(String.format("if (%s) { %s }\n", cnd.getConditionObject(), cnd.getInnerFrame()));
            } else {
                str.append(String.format("{ %s }\n", cnd.getInnerFrame()));
            }            
        }
        
        return str.toString();
    }
    
    public static class Conditional {
        private Frame outerFrame, innerFrame;
        private ExpressionObject condition;
        
        /**
         * @param condition EnumExpression.EXPRESSION
         * @param body EnumExpression.BLOCK
         */
        public Conditional(Frame parent, Node condition, Node body) {
            this.outerFrame = new Frame(parent, null);
            this.innerFrame = new Frame(outerFrame, body);
            
            if (condition != null) {
                this.condition = outerFrame.handleExpression(condition).getAsExpObj();
            }
        }
        
        /**
         * @param bodyHead EnumExpression.BLOCK
         */
        public Conditional(Frame parent, Node bodyHead) {
            this(parent, null, bodyHead);
        }
        
        public boolean hasCondition() {
            return condition != null;
        }
        
        public ExpressionObject getConditionObject() {
            return condition;
        }
        
        public Frame getInnerFrame() {
            return innerFrame;
        }
        
        public Frame getOuterFrame() {
            return outerFrame;
        }
    }
}
