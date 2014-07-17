package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;
import lombok.NonNull;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;

public class BasicObjectHolder extends BasicObject {

    @Delegate(types = BasicObject.class, excludes = IHolderExcludeList.class)
    private BasicObject heldObj;
    private Type type;

    public BasicObjectHolder(BasicObject obj, @NonNull Type type) {
        super(obj.getName(), type);

        this.heldObj = obj;
        this.type = obj.getType().castTo(type);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public BasicObject getForUse(Statement s) {
        heldObj.getForUse(s);

        return this;
    }
}
