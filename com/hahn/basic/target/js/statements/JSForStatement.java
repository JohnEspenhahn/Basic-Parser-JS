package com.hahn.basic.target.js.statements;

import java.util.List;

import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;

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
        if (getModifyStatements() != null) {
            List<OPObject> modify = getModifyStatements();
            for (int i = modify.size() - 1; i >= 0; i--) {
                modify.get(i).getForUse(this);
            }
        }
        
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
        
        if (getModifyStatements() != null) {
            List<OPObject> modify = getModifyStatements();
            for (int i = 0; i < modify.size(); i++) {
                modify.get(i).takeRegister(this);
            }
        }
        
        if (getConditionStatement() != null) {
            // Make sure don't free register before handling frame
            getConditionStatement().forwardOptimize();
        }
        
        return false;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        return String.format("for(%s;%s;%s){%s}", 
                getDefineStatement().toTarget(builder),
                getConditionStatement().toTarget(builder),
                getModifyStatements().toTarget(builder),
                getInnerFrame().toTarget(builder));
    }
}