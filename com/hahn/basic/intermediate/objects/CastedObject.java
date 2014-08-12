package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;

public abstract class CastedObject extends BasicObject {

    interface IHolderExcludeList {
        public Type getType();
        public BasicObject castTo(Type t);
        public ExpressionStatement getAsExp(Statement container);
        
        // Explicitly implemented functions
        public void setType(Type t);
        public boolean setInUse(IIntermediate by);
        public void takeRegister(IIntermediate by);
    }
    
    @Delegate(types = BasicObject.class, excludes = IHolderExcludeList.class)
    private BasicObject heldObj;
    
    private int row, col;

    /**
     * A holder for an object, usually for a casted object
     * @param obj The object to hold
     * @param type The new type of the object to be held
     * @param row The row to throw an error at
     * @param col The column to throw an error at
     */
    public CastedObject(BasicObject obj, Type type, int row, int col) {
        super(obj.getName(), type);

        this.heldObj = obj;
        
        this.row = row;
        this.col = col;
    }
    
    /**
     * Get the object being held
     * @return The object being held
     */
    protected BasicObject getHeldObject() {
        return heldObj;
    }
    
    /**
     * Update the type of both the holder
     * and the held object
     * @param t The new type
     */
    @Override
    public void setType(Type t) {
        super.setType(t);
        this.heldObj.setType(t);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        Type pretype = getHeldObject().getType();
        boolean result = getHeldObject().setInUse(this);
        
        // If held type changed, verify cast
        if (getHeldObject().getType() != pretype) {
            getHeldObject().getType().castTo(getType(), this.row, this.col);
        }
        
        return result;
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        Type pretype = getHeldObject().getType();
        getHeldObject().takeRegister(this);
        
        // If held type changed, verify cast
        if (getHeldObject().getType() != pretype) {
            getHeldObject().getType().castTo(getType(), this.row, this.col);
        }
    }
}
