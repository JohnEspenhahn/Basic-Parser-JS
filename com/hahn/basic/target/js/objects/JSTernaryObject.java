package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.TernaryObject;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

public class JSTernaryObject extends TernaryObject {
    
    public JSTernaryObject(Statement container, BasicObject condition, Node node_then, Node node_else, CodeFile file, int row, int col) {
        super(container, condition, node_then, node_else, file, row, col);
    }
    
    @Override
    public String toTarget() {
        return String.format("%s_?_%s_:_%s",
                doGroup(getConditional()) ? "("+getConditional().toTarget()+")" : getConditional().toTarget(),
                doGroup(getThen()) ? "("+getThen().toTarget()+")" : getThen().toTarget(),
                doGroup(getElse()) ? "("+getElse().toTarget()+")" : getElse().toTarget());
    }
    
}
