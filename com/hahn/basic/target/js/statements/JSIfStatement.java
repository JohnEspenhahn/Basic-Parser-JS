package com.hahn.basic.target.js.statements;

import java.util.List;
import java.util.ListIterator;

import com.hahn.basic.intermediate.statements.IfStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public class JSIfStatement extends IfStatement {
    
    public JSIfStatement(Statement container, List<Conditional> elseStatements) {
        super(container, elseStatements);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        ListIterator<Conditional> it = getConditionals().listIterator(getConditionals().size());
        while (it.hasPrevious()) {
            Conditional cnd = it.previous();
            
            cnd.getInnerFrame().reverseOptimize();
            
            if (cnd.getConditionStatement() != null) {
                cnd.getConditionStatement().reverseOptimize();
            }
        }
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        for (Conditional cnd: getConditionals()) {
            cnd.getInnerFrame().forwardOptimize();
            
            if (cnd.getConditionStatement() != null) {
                // Make sure don't free register before handling frame
                cnd.getConditionStatement().forwardOptimize();
            }
        }
        
        return false;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        StringBuilder str = new StringBuilder();
        
        boolean first = true;
        for (Conditional cnd: getConditionals()) {
            if (!first) str.append("else ");
            else first = false;
            
            if (cnd.hasCondition()) {
                str.append(String.format("if(%s){%s}", 
                        cnd.getConditionStatement().toTarget(builder), 
                        cnd.getInnerFrame().toTarget(builder)));
            } else {
                str.append(String.format("{%s}", 
                        cnd.getInnerFrame().toTarget(builder)));
            }            
        }
        
        return str.toString();
    }
    
}
