package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.asm.raw.ASMCommand;
import com.hahn.basic.util.CompileException;

public class ContinueStatement extends Statement {
    private final Label lblStart;
    
    public ContinueStatement(Statement s) {
        super(s);
        
        Frame loop = getFrame().getLoop();
        if (loop == null) {
            throw new CompileException("Invalid use of `continue`");
        }
        
        lblStart = loop.getStartLabel();
    }
    
    @Override
    public void addTargetCode() {
        addCode(new ASMCommand(OPCode.MOV, lblStart.toTarget()));
    }

    @Override
    public String toString() {
        return "continue " + lblStart;
    }
}
