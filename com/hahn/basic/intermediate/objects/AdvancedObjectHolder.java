package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.objects.types.Type;

public class AdvancedObjectHolder extends AdvancedObject implements IHolderExcludeList {

    @Delegate(types=AdvancedObject.class, excludes=IHolderExcludeList.class)
    private AdvancedObject heldObj;
    
    public AdvancedObjectHolder(AdvancedObject obj, Type type) {
        super(obj.getFrame(), obj.getName(), obj.getType().castTo(type));
        
        this.heldObj = obj;
    }

    protected BasicObject getHeldObject() {
        return heldObj;
    }
}
