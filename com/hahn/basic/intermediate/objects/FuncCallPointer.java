package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class FuncCallPointer extends FuncPointer {
    private Type returnType;

    public FuncCallPointer(String name, BasicObject[] params) {
        super(name, new ParameterizedType<ITypeable>(Type.FUNC, (ITypeable[]) params));

        this.returnType = Type.UNDEFINED;
    }

    public boolean isUsed() {
        return getUses() > 0;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    /**
     * Cast to given type
     * @param t The type to cast to
     * @return this
     */
    @Override
    public BasicObject castTo(Type t) {
        this.returnType = this.returnType.castTo(t);

        return this;
    }

    @Override
    public boolean setInUse(IIntermediate by) {
        checkFunction();
        returnType = func.getReturnType().castTo(returnType);
        
        return super.setInUse(by);
    }

    @SuppressWarnings("unchecked")
    public BasicObject[] getParams() {
        return ((ParameterizedType<BasicObject>) super.getType()).getTypes();
    }

    @Override
    public String toString() {
        return super.toString() + "(...)";
    }
}