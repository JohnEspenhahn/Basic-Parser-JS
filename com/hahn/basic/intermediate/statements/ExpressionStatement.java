package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.objects.ExpressionObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class ExpressionStatement extends Statement {
    private IBasicObject obj;
    private Node node;
    
    private boolean forcedGroup;
    
    private boolean gotAsObject;
    
    public ExpressionStatement(Statement continer, IBasicObject obj) {
        super(continer);
        
        this.obj = obj;        
        this.forcedGroup = false;
        this.gotAsObject = false;
    }
    
    /**
     * Set to require grouping for circumstances not normally obvious (ex: random use of paranthesis)
     * @param b The value to set `force grouping` to
     */
    public void setForcedGroup(boolean b) {
        this.forcedGroup = b;
    }
    
    /**
     * If true grouping is required for this expression statement. Either because it is being forced or the held object is grouped
     * @return True if needs grouping
     */
    public boolean isGrouped() {
        return forcedGroup || getObj().isGrouped();
    }
    
    public IBasicObject getObj() {
        return obj;
    }
    
    public void setObj(ExpressionStatement otherExp, Node node) {
        setObj(otherExp.getAsExpObj(), node);
    }
    
    public void setObj(IBasicObject obj, Node node) {
        enforce();
        
        this.obj = obj;
        this.node = node;
    }
    
    public Node getNode() {
        return node;
    }
    
    public void castTo(Type type, CodeFile file, int row, int col) {
        enforce();
        
        this.obj = this.obj.castTo(type, file, row, col);
    }
    
    @Override
    public boolean reverseOptimize() {
        if (node != null) getFile().pushCurrentPoint(node.getRow(), node.getCol());   
        
        getObj().setInUse(this);
        
        if (node != null) getFile().popCurrentPoint();
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        getFile().pushCurrentLine(row);
        
        getObj().takeRegister(this);
        
        getFile().popCurrentPoint();
        
        return false;
    }
    
    /**
     * Get an object version of this statement. Should only
     * be called after the statement has been fully evaluated
     * @return ExpressionStatementObject
     */
    public ExpressionObject getAsExpObj() {
        this.gotAsObject = true;
        
        return getFactory().ExpressionObject(this);
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
