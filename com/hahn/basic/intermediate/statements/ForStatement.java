package com.hahn.basic.intermediate.statements;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.asm.raw.ASMLabel;

public class ForStatement extends Statement {
    private Node define;
    private List<Node> modification;
    private Conditional conditional;
    
    /**
     * @param s Owning statement
     * @param define EnumExpression.DEFINE
     * @param condition EnumExpression.EXPRESSION
     * @param modification List of EnumExpression.MODIFY
     * @param body EnumExpression.BLOCK
     */
    public ForStatement(Statement s, Node define, Node condition, List<Node> modification, Node body) {
        super(s);
        
        this.define = define;
        this.modification = modification;
        
        this.conditional = new Conditional(condition, body);
    }

    @Override
    public void addTargetCode() {
        Label lblStart = new Label(getFrame().getLabel("@for_start"));
        Label lblContinue = new Label(getFrame().getLabel("@for_continue"));
        Label lblEnd = new Label(getFrame().getLabel("@for_end"));
        
        Frame f = new Frame(getFrame());
        f.defineVar(define);        
        f.addCode(new ASMLabel(lblStart));        
        f.handleConditional(conditional, lblContinue, lblEnd, false);
        f.addCode(new ASMLabel(lblContinue));
        
        for (Node n: modification) {
            f.modifyVar(n);
        }
        
        // Default condition to false, therefore wouldn't loop
        if (conditional.getConditionHead() != null) {
            f.addCode(new Command(f, OPCode.MOV, lblStart));
        }
        
        addCode(f);
        addCode(new ASMLabel(lblEnd));
    }

}
