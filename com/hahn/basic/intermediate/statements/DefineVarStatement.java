package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.LangBuildTarget;

public class DefineVarStatement extends Statement {    
    private final BasicObject var;
    private final BasicObject val;
    private final boolean ignoreTypeCheck;
    
    public DefineVarStatement(Statement s, BasicObject var, BasicObject val) {
        this(s, var, val, false);
    }
    
    public DefineVarStatement(Statement s, BasicObject var, BasicObject val, boolean ignoreTypeCheck) {
        super(s);
        
        this.val = val;
        this.var = var;
        this.ignoreTypeCheck = ignoreTypeCheck;
    }
    
    public BasicObject getVar() {
        return var;
    }
    
    public BasicObject getVal() {
        return val;
    }
    
    @Override
    public void addTargetCode() {
        addCode(new Command(this, OPCode.SET, var.getForCreateVar(), val));
    }
    
    @Override
    public boolean reverseOptimize() {        
        // Type check
        if (!ignoreTypeCheck) {
            Main.setLine(row);
            Type.merge(var.getType(), val.getType());
        }
        
        super.reverseOptimize();
        
        var.removeInUse();
        if (var.getUses() == 1) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean forwardOptimize() {
        if (var.canSetLiteral() && val.hasLiteral()) {
            var.setLiteral(val.getLiteral());
        }
        
        return super.forwardOptimize();
    }
    
    @Override
    public void toTarget(LangBuildTarget builder) {
        if (!var.hasLiteral()) {
            super.toTarget(builder);
        }
    }
}
