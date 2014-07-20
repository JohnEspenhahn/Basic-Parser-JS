package com.hahn.basic.target.js.statements;

import java.util.List;

import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

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
        List<Compilable> modify = getModifyStatements();
        for (int i = modify.size() - 1; i >= 0; i--) {
            modify.get(i).reverseOptimize();
        }
        
        getInnerFrame().reverseOptimize();
        
        getConditionObject().setInUse(this);
        
        List<Compilable> define = getDefineStatements();
        for (int i = define.size() - 1; i >= 0; i--) {
            define.get(i).reverseOptimize();
        }
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        List<Compilable> define = getDefineStatements();
        for (int i = 0; i < define.size(); i++) {
            define.get(i).forwardOptimize();
        }
        
        getConditionObject().takeRegister(this);
        
        getInnerFrame().forwardOptimize();
        
        List<Compilable> modify = getModifyStatements();
        for (int i = 0; i < modify.size(); i++) {
            modify.get(i).forwardOptimize();
        }
        
        return false;
    }
}
