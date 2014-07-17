package com.hahn.basic.intermediate.statements;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.asm.raw.ASMLabel;

public class IfStatement extends Statement {
    
    private List<Conditional> elseStatements;
    private Label lblFinalEnd;
    
    public IfStatement(Statement s, List<Conditional> elseStatements) {
        super(s);
        
        this.elseStatements = elseStatements;
        this.lblFinalEnd = new Label(getFrame().getLabel("@if_end"));
    }

    @Override
    public void addTargetCode() {
        for (int i = 0; i < elseStatements.size(); i++) {
            Conditional cnd = elseStatements.get(i);
            Label lblSkip = new Label(getFrame().getLabel("@if_skip"));
            
            Frame f = new Frame(getFrame());
            f.handleConditional(cnd, lblFinalEnd, lblSkip);
            addCode(f);
            
            addCode(new ASMLabel(lblSkip));
        }
        addCode(new ASMLabel(lblFinalEnd));
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
