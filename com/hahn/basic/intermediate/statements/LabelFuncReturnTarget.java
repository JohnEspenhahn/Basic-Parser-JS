package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.intermediate.register.StackRegister;
import com.hahn.basic.target.asm.raw.ASMLabel;

public class LabelFuncReturnTarget extends Statement {    
    private final Label lbl;
    private final int numParams;
    
    public LabelFuncReturnTarget(Statement s, Label lbl, BasicObject[] params) {
        super(s);
        
        this.lbl = lbl;
        this.numParams = params.length;
    }
    
    @Override
    public void addTargetCode() {
        addCode(new ASMLabel(lbl));
    }
    
    @Override
    public boolean forwardOptimize() {
        StackRegister.pop(1 + numParams);
        
        return super.forwardOptimize();
    }

    @Override
    public String toString() {
        return lbl.getName() + ":";
    }
}
