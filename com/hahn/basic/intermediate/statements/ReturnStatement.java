package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.register.Register;
import com.hahn.basic.target.asm.statements.ASMCompilableRecoverRegs;

public class ReturnStatement extends Statement {
    public static final int MIN_REGS_FOR_PUSHA = 4;
    
    private FuncHead func;
    private AdvancedObject[] currentVars;
    
    public ReturnStatement(Statement s, FuncHead returnFrom) {
        super(s);
        
        this.func = returnFrom;
        this.currentVars = returnFrom.getCurrentVars();
    }
    
    @Override
    public void addTargetCode() {  
        addCode(new ASMCompilableRecoverRegs(func));
        
        // Clear stack registers used at this point
        int stackRegsHere = 0;
        for (AdvancedObject var: currentVars) {
            if (var.isRegisterOnStack()) {
                stackRegsHere += 1;
            }
        }
        
        if (stackRegsHere > 0) {
            addCode(new Command(this, OPCode.SPA, new LiteralNum(stackRegsHere)));
        }
        
        // Return
        addCode(new Command(this, OPCode.MOV, Register.POP));
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof ReturnStatement) {
            ReturnStatement r = (ReturnStatement) o;
            return func.equals(r.func);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "DoReturn";
    }
}
