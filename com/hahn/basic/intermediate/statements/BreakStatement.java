package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.asm.raw.ASMCommand;
import com.hahn.basic.util.CompileException;

public class BreakStatement extends Statement {
    private final Label lblEnd;
    
    public BreakStatement(Statement s) {
        super(s);
        
        Frame loop = getFrame().getLoop();
        if (loop == null) {
            throw new CompileException("Invalid use of `break`");
        }
        
        lblEnd = loop.getEndLabel();
    }

    @Override
    public void addTargetCode() {
        addCode(new ASMCommand(OPCode.MOV, lblEnd.toTarget()));   
    }
    
    @Override
    public String toString() {
        return "break " + lblEnd;
    }

}
