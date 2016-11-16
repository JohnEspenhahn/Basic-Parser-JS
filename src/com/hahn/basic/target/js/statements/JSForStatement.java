package com.hahn.basic.target.js.statements;

import java.util.List;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.JSPretty;

public class JSForStatement extends ForStatement {
    
    public JSForStatement(Statement continer, Node define, Node condition, List<Node> modification, Node body) {
        super(continer, define, condition, modification, body);
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        getModifyFrame().reverseOptimize();
        
        getInnerFrame().reverseOptimize();
        
        if (getConditionStatement() != null) {
            getConditionStatement().reverseOptimize();
        }
        
        if (getDefineStatement() != null) {
            getDefineStatement().reverseOptimize();
        }
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        if (getDefineStatement() != null) {
            getDefineStatement().forwardOptimize();
        }
        
        getInnerFrame().forwardOptimize();
        getModifyFrame().forwardOptimize();      
        
        if (getConditionStatement() != null) {
            // Make sure don't free register before handling frame
            getConditionStatement().forwardOptimize();
        }
        
        return false;
    }
    
    @Override
    public String toTarget() {
        return JSPretty.format(getFile().isPretty(), 0, "for(%S;_%S;_%L)%b",
                getDefineStatement(),
                getConditionStatement(),
                getModifyFrame().getTargetCode().toArray(new IIntermediate[getModifyFrame().getTargetCode().size()]),
                getInnerFrame());
    }
}
