package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.target.asm.raw.ASMLabel;

public class WhileStatement extends Statement {
    Conditional conditional;
    
    public WhileStatement(Statement s, Conditional conditional) {
        super(s);
        
        this.conditional = conditional;
    }

    @Override
    public void addTargetCode() {
        Label lblStart = new Label(getFrame().getLabel("@if_start"));
        Label lblEnd = new Label(getFrame().getLabel("@if_end"));
        
        Frame f = new Frame(getFrame());
        f.addCode(new ASMLabel(lblStart));
        f.handleConditional(conditional, lblStart, lblEnd);
        f.addCode(new ASMLabel(lblEnd));
        addCode(f);
    }

}
