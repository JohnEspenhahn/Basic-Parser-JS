package com.hahn.basic.target.js.objects;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.CastedObject;
import com.hahn.basic.intermediate.objects.types.Type;

public class JSCastedObject extends CastedObject {
    
    public JSCastedObject(BasicObject obj, Type type, int row, int col) {
        super(obj, type, row, col);
    }
    
    private boolean needsIntCast() {
        return getType().doesExtend(Type.INT) && !getHeldObject().getType().doesExtend(Type.INT);
    }
    
    @Override
    public boolean isGrouped() {
        return needsIntCast() || super.isGrouped();
    }
    
    @Override
    public String toTarget() {
        // Special int cast
        if (needsIntCast()) {
            return super.toTarget() + "|0";
        } else {
            return super.toTarget();
        }
    }
}
