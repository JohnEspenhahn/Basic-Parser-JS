package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.TernaryObject;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;

public class JSTernaryObject extends TernaryObject {
    
    public JSTernaryObject(Statement container, BasicObject condition, Node node_then, Node node_else) {
        super(container, condition, node_then, node_else);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("%s?%s:%s",
                doGroup(getConditional()) ? "("+getConditional().toTarget(builder)+")" : getConditional().toTarget(builder),
                doGroup(getThen()) ? "("+getThen().toTarget(builder)+")" : getThen().toTarget(builder),
                doGroup(getElse()) ? "("+getElse().toTarget(builder)+")" : getElse().toTarget(builder));
    }
    
}
