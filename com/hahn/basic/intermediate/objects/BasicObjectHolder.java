package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;
import lombok.NonNull;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;

public class BasicObjectHolder extends BasicObject implements IHolderExcludeList {

    @Delegate(types = BasicObject.class, excludes = IHolderExcludeList.class)
    private BasicObject heldObj;

    public BasicObjectHolder(BasicObject obj, @NonNull Type type) {
        super(obj.getName(), obj.getType().castTo(type));

        this.heldObj = obj;
    }
    
    protected BasicObject getHeldObject() {
        return heldObj;
    }

    @Override
    public BasicObject getForUse(Statement s) {
        heldObj.getForUse(s);

        return this;
    }
}
