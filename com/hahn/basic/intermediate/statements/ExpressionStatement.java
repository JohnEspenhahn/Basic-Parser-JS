package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ExpressionObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class ExpressionStatement extends Statement {
    private BasicObject obj;
    private Node node;
    
    private boolean forcedGroup;
    
    private boolean gotAsObject;
    
    public ExpressionStatement(Statement continer, BasicObject obj) {
        super(continer);
        
        this.obj = obj;        
        this.forcedGroup = false;
        this.gotAsObject = false;
    }
    
    public void setForcedGroup(boolean b) {
        this.forcedGroup = b;
    }
    
    public boolean isForcedGroup() {
        return forcedGroup;
    }
    
    public BasicObject getObj() {
        return obj;
    }
    
    public void setObj(ExpressionStatement otherExp, Node node) {
        setObj(otherExp.getAsExpObj(), node);
    }
    
    public void setObj(BasicObject obj, Node node) {
        enforce();
        
        this.obj = obj;
        this.node = node;
    }
    
    public Node getNode() {
        return node;
    }
    
    public void castTo(Type type, int row, int col) {
        enforce();
        
        this.obj = this.obj.castTo(type, row, col);
    }
    
    @Override
    public boolean reverseOptimize() {
        int oldRow = Main.getRow();
        int oldCol = Main.getCol();
        
        if (node != null) Main.setLine(node.getRow(), node.getCol());   
        
        getObj().setInUse(this);
        
        Main.setLine(oldRow, oldCol);
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        int oldRow = Main.getRow();
        int oldCol = Main.getCol();
        
        Main.setLine(row, 0);
        
        getObj().takeRegister(this);
        
        Main.setLine(oldRow, oldCol);
        
        return false;
    }
    
    /**
     * Get an object version of this statement. Should only
     * be called after the statement has been fully evaluated
     * @return ExpressionStatementObject
     */
    public ExpressionObject getAsExpObj() {
        this.gotAsObject = true;
        
        return LangCompiler.factory.ExpressionObject(this);
    }
    
    /**
     * Ensure valid state
     */
    public void enforce() {
        if (gotAsObject) {
            throw new RuntimeException("Tried to modify ExpressionStatement after getting as ExpressionObject!");
        }
    }
    
    @Override
    public String toString() {
        return getObj().toString();
    }
}
