package com.hahn.basic.intermediate.statements;

import java.util.List;

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
    
    public static class Conditional {
        private final Node bodyHead;
        private final Node conditionHead;
        
        /**
         * @param conditionHead EnumExpression.EXPRESSION
         * @param bodyHead EnumExpression.BLOCK
         */
        public Conditional(Node conditionHead, Node bodyHead) {
            this.conditionHead = conditionHead;
            this.bodyHead = bodyHead;
        }
        
        /**
         * @param bodyHead EnumExpression.BLOCK
         */
        public Conditional(Node bodyHead) {
            this(null, bodyHead);
        }
        
        public Node getConditionHead() {
            return conditionHead;
        }
        
        public Node getBodyHead() {
            return bodyHead;
        }
    }
}
