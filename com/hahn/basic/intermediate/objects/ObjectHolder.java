package com.hahn.basic.intermediate.objects;

import lombok.NonNull;
import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;

public class ObjectHolder extends BasicObject {

    interface IHolderExcludeList {
        public Type getType();
        public void setType(Type t);
        public BasicObject castTo(Type t);
        public ExpressionStatement getAsExp(Statement container);
    }
    
    @Delegate(types = BasicObject.class, excludes = IHolderExcludeList.class)
    private BasicObject heldObj;
    
    protected ObjectHolder(BasicObject obj) {
        this(obj, obj.getType(), -1, -1);
    }

    public ObjectHolder(BasicObject obj, @NonNull Type type, int row, int col) {
        super(obj.getName(), obj.getType().castTo(type, row, col));

        this.heldObj = obj;
    }
    
    protected BasicObject getHeldObject() {
        return heldObj;
    }
}
