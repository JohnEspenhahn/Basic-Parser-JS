package com.hahn.basic.intermediate.statements.function;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.register.Register;
import com.hahn.basic.intermediate.statements.Command;
import com.hahn.basic.intermediate.statements.LabelFuncReturnTarget;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.util.Util;

public class CallFuncStatement extends Statement {    
    private FuncCallPointer funcCallPointer;
    
    public CallFuncStatement(Statement s, FuncCallPointer fcp) {
        super(s);
        
        this.funcCallPointer = fcp;
    }
    
    public FuncCallPointer getFuncCallPointer() {
        return funcCallPointer;
    }
    
    public BasicObject[] getParams() {
        return funcCallPointer.getParams();
    }
    
    @Override
    public void addTargetCode() {
        funcCallPointer.doAddPreCall(this);
        
        BasicObject[] params = getParams();
        Label rtnLabel = new Label(getFrame().getLabel("@rtn_" + funcCallPointer.getName()));
        
        if (funcCallPointer.getTypes().length == 0) {
            addCode(new Command(this, OPCode.JSR, new Label(funcCallPointer.getFuncId())));
        } else {
            // Push return location
            addCode(new Command(this, OPCode.SET, (BasicObject) Register.PUSH, rtnLabel));
            
            // Push all parameters, cleared within function
            for (int i = 0; i < params.length; i++) {
                addCode(new Command(this, OPCode.SET, (BasicObject) Register.PUSH, params[i]));
            }
            
            // Move to function
            addCode(new Command(this, OPCode.MOV, new Label(funcCallPointer.getFuncId())));
            
            // Push return label
            addCode(new LabelFuncReturnTarget(this, rtnLabel, params));            
        }
        
        funcCallPointer.doAddPostCall(this);
    }
    
    @Override
    public boolean reverseOptimize() {
        funcCallPointer.setInUse();
        
        return super.reverseOptimize();
    }
    
    @Override
    public String toString() {
        return FuncHead.toHumanReadable(funcCallPointer) + "(" + Util.toString(getParams(), ", ") + ")";
    }
}
