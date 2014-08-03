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

    public ObjectHolder(BasicObject obj, @NonNull Type type) {
        super(obj.getName(), type);

        this.heldObj = obj;
    }
    
    protected BasicObject getHeldObject() {
        return heldObj;
    }
}
