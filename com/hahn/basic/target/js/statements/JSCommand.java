package com.hahn.basic.target.js.statements;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Command;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSCommand extends Command {
    
    public JSCommand(Statement container, OPCode opcode, BasicObject p1, BasicObject p2) {
        super(container, opcode, p1, p2);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("%s%s%s", 
                getP1().toTarget(builder), 
                getOP().getSymbol(), 
                getP2().toTarget(builder));
    }
    
}
