package com.hahn.basic.intermediate.objects;

import lombok.NonNull;
import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.objects.types.Type;

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
}
