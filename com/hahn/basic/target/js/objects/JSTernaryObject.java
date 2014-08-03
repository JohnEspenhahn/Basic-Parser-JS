package com.hahn.basic.target.js.objects;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.TernaryObject;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

public class JSTernaryObject extends TernaryObject {
    
    public JSTernaryObject(Statement container, BasicObject condition, Node node_then, Node node_else) {
        super(container, condition, node_then, node_else);
    }
    
    @Override
    public String toTarget() {
        return String.format("%s%s%s%s%s",
                doGroup(getConditional()) ? "("+getConditional().toTarget()+")" : getConditional().toTarget(),
                (Main.PRETTY_PRINT ? " ? " : "?"),
                doGroup(getThen()) ? "("+getThen().toTarget()+")" : getThen().toTarget(),
                (Main.PRETTY_PRINT ? " : " : ":"),
                doGroup(getElse()) ? "("+getElse().toTarget()+")" : getElse().toTarget());
    }
    
}
