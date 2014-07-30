package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.exceptions.CompileException;

public class JSOPObject extends OPObject {
    
    public JSOPObject(Statement container, OPCode opcode, BasicObject p1, BasicObject p2) {
        super(container, opcode, p1, p2);
    }
    
    @Override
    public BasicObject getForUse(Statement s) {
        return this;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        if (OPCode.doesModify(getOP())&& getP1().hasFlag("const")) {
            throw new CompileException("Can not modify the constant variable `" + getP1() + "`");
        }
        
        return super.setInUse(by);
    }
    
    @Override
    public String doToTarget(LangBuildTarget builder) {
        return String.format("%s%s%s", getP1().toTarget(builder), getOP().getSymbol(), getP2().toTarget(builder));
    }    
}
